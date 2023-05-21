package com.rysiw.chatgptmanage.gptmanager.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rysiw.chatgptmanage.common.constant.UserErrorBuilder;
import com.rysiw.chatgptmanage.common.dto.UserQueryDataDTO;
import com.rysiw.chatgptmanage.common.dto.UserStatisticsInfoDTO;
import com.rysiw.chatgptmanage.common.enums.CommonConstant;
import com.rysiw.chatgptmanage.common.enums.RespCode;
import com.rysiw.chatgptmanage.common.exception.DefineException;
import com.rysiw.chatgptmanage.common.utils.UserAccessStatistics;
import com.rysiw.chatgptmanage.common.vo.ResultVO;
import com.rysiw.chatgptmanage.gptmanager.common.dto.ChatCompletionRequestDTO;
import com.rysiw.chatgptmanage.gptmanager.common.dto.ChatMessageDTO;
import com.rysiw.chatgptmanage.gptmanager.config.OpenAiConfig;
import com.rysiw.chatgptmanage.gptmanager.common.dto.OpenAiRequest;
import com.theokanning.openai.completion.chat.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class OpenAiService {

    private static final Logger ELK_LOGGER = LoggerFactory.getLogger("ELK_LOGGER");

    @Resource
    private OpenAiConfig openAiConfig;
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserAccessStatistics userAccessStatistics;

    public ChatCompletionResult myQuery(ChatCompletionRequestDTO chatCompletionRequestDTO){
        String userToken = request.getHeader("userToken");
        userAccessStatistics.incrUserAccessCount(userToken);
        long queryCount = userAccessStatistics.getUserAccessCount(userToken);
        UserStatisticsInfoDTO queryInfoDTO = UserStatisticsInfoDTO.builder()
                .userToken(userToken)
                .queryCount(String.valueOf(queryCount))
                .chatCompletionRequestDTO(chatCompletionRequestDTO)
                .build();
//        openAiConfig.getApiKey();
//        if(Objects.nonNull(userToken)){
//            throw new RuntimeException();
//        }

        ELK_LOGGER.info("用户请求信息" + JSONUtil.toJsonStr(queryInfoDTO));
        try {
            // 获取apikey
            String apikey = openAiConfig.getApiKey();
            if (StrUtil.isBlank(apikey))
                throw new DefineException(RespCode.INTERNAL_ERROR, UserErrorBuilder
                        .builder()
                        .userToken(request.getHeader("userToken"))
                        .errorMsg("OpenAi-Token为空")
                        .apiKey(apikey)
                        .build());

            //  获取最大返回字符数
            com.theokanning.openai.service.OpenAiService service = new com.theokanning.openai.service.OpenAiService(apikey, Duration.ofSeconds(29));
            chatCompletionRequestDTO.setModel(openAiConfig.getModel());
            chatCompletionRequestDTO.setTemperature(openAiConfig.getTemperature());
            chatCompletionRequestDTO.setMaxTokens(openAiConfig.getMaxTokens());
            List<ChatMessageDTO> chatMessageList = chatCompletionRequestDTO.getMessages();
            chatCompletionRequestDTO.setMessages(ListUtil.sub(chatCompletionRequestDTO.getMessages(), Math.max((chatMessageList.size() - CommonConstant.ChatContextLength), 0), chatMessageList.size()));
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().build();
            BeanUtil.copyProperties(chatCompletionRequestDTO, chatCompletionRequest);
            ChatCompletionResult result = service.createChatCompletion(chatCompletionRequest);
            UserQueryDataDTO userQueryDataDTO = UserQueryDataDTO.builder()
                    .userToken(userToken)
                    .question(JSONUtil.toJsonStr(chatCompletionRequest.getMessages()))
                    .answer(result.getChoices().get(0).getMessage().getContent())
                    .build();
            queryInfoDTO = UserStatisticsInfoDTO.builder()
                    .userToken(userToken)
                    .queryCount(String.valueOf(queryCount))
                    .chatCompletionResult(result)
                    .build();
            ELK_LOGGER.info("用户返回信息" + JSONUtil.toJsonStr(queryInfoDTO));
            ELK_LOGGER.info("用户问答信息" + JSONUtil.toJsonStr(userQueryDataDTO));
            result.setId("");
            result.setCreated(1);
            result.setUsage(null);
            return result;
        }catch (Exception e){
            log.error("token: " + request.getHeader("userToken") + ", 请求ChatGPT错误: ", e);
            throw new DefineException(RespCode.REQUEST_OPENAI_ERROR);
        }
    }

    public String creditQuery(){
        log.debug("test");
        StringBuilder sb = new StringBuilder();
        for(String key : openAiConfig.getKeys().split(",")){
            sb.append(key);
            sb.append(" ");
            sb.append(creditQuery(key));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 查询余额
     * @param apikey /
     * @return /
     */
    public String creditQuery(String apikey){
        try {
            // 调用接口
            String result = HttpRequest.get(openAiConfig.getCreditApi())
                    .header(Header.CONTENT_TYPE, "application/json")
                    .header(Header.AUTHORIZATION, "Bearer " + apikey)
                    .execute().body();
            // 判断是否请求出错
            if(result.contains("server_error")){
                throw new DefineException(RespCode.REQUEST_OPENAI_ERROR);
            }
            // 解析结果
            JSONObject jsonObject = JSONUtil.parseObj(result);
            // 返回结果
            return "余额剩余：" + jsonObject.getStr("total_available");
        } catch (Exception e){
            log.error("请求ChatGPT错误", e);
            throw new DefineException(RespCode.REQUEST_OPENAI_ERROR);
        }
    }

    /**
     * 问答，绘画
     *
     * @param openAiDto       /
     */
    public ResultVO communicate(OpenAiRequest openAiDto) {
        // 获取apikey
        String apikey = openAiDto.getApikey();
        if(StrUtil.isBlank(apikey)){
            apikey = openAiConfig.getApiKey();
            if(StrUtil.isBlank(apikey))
                throw new DefineException(RespCode.INTERNAL_ERROR, UserErrorBuilder
                        .builder()
                        .userToken(openAiDto.getUserToken())
                        .errorMsg("OpenAi-Token为空")
                        .apiKey(apikey)
                        .build());
        }
        //  获取最大返回字符数
        Integer maxTokens = openAiConfig.getMaxTokens();
        // 根据id判断调用哪个接口
        try {
            switch (openAiDto.getType()){
                // 文本问答
                case 1:
                    return ResultVO.builder().data(textQuiz(maxTokens, openAiDto, apikey)).build();
                // 图片生成
                case 2:
                    return ResultVO.builder().data(imageQuiz(openAiDto, apikey)).build();
                // 默认
                default:
                    return ResultVO.buildError("出错了：未知的请求类型");
            }
        } catch (Exception e){
            e.printStackTrace();
            return ResultVO.builder().msg("出错了：" + e.getMessage()).build();
        }
    }

    private String textQuiz(Integer maxTokens, OpenAiRequest openAiRequest, String apikey) throws Exception {
        // 构建对话参数
        List<Map<String, String>> messages = new ArrayList<>();
        // 如果是连续对话，逐条添加对话内容
        if("1".equals(openAiRequest.getKeep())){
            String[] keepTexts = openAiRequest.getKeepText().split("\n");
            for(String keepText : keepTexts){
                String[] split = keepText.split("・・");
                for(String str : split){
                    String[] data = str.split(":");
                    if(data.length < 2){
                        continue;
                    }
                    String role = data[0];
                    String content = data[1];
                    Map<String, String> userMessage = MapUtil.ofEntries(
                            MapUtil.entry("role", role),
                            MapUtil.entry("content", content)
                    );
                    messages.add(userMessage);
                }
            }
        } else {
            Map<String, String> userMessage = MapUtil.ofEntries(
                    MapUtil.entry("role", "user"),
                    MapUtil.entry("content", openAiRequest.getContent())
            );
            messages.add(userMessage);
        }

        // 构建请求参数
        Map<String, Object> params = MapUtil.ofEntries(
                MapUtil.entry("stream", true),
                MapUtil.entry("max_tokens", maxTokens),
                MapUtil.entry("model", openAiConfig.getModel()),
                MapUtil.entry("temperature", openAiConfig.getTemperature()),
                MapUtil.entry("messages", messages)
        );
        // 调用接口
        HttpResponse result;
        try {
            result = HttpRequest.post(openAiConfig.getOpenaiApi())
                    .header(Header.CONTENT_TYPE, "application/json")
                    .header(Header.AUTHORIZATION, "Bearer " + apikey)
                    .body(JSONUtil.toJsonStr(params))
                    .executeAsync();
        }catch (Exception e){
            e.printStackTrace();
            return ("出错了：" + e.getMessage());
        }
        // 处理数据
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(result.bodyStream()));
        boolean flag = false;
        boolean printErrorMsg = false;
        StringBuilder errMsg = new StringBuilder();
        while((line = reader.readLine()) != null){
            String msgResult = UnicodeUtil.toString(line);
            // 正则匹配错误信息
            if(msgResult.contains("\"error\":")){
                printErrorMsg = true;
            }
            // 如果出错，打印错误信息
            if (printErrorMsg) {
                log.error(msgResult);
                errMsg.append(msgResult);
            }
            // 正则匹配结果
            Matcher m = Pattern.compile("\"content\":\"(.*?)\"").matcher(msgResult);
            if(m.find()) {
                // 将\n和\t替换为html中的换行和制表，将\替换为"
                String data = m.group(1).replace("\\n", "\n")
                        .replace("\\t", "\t")
                        .replace("\\", "\"");
                // 过滤AI回复开头的换行
                if(!data.matches("\\n+") && !flag) {
                    flag = true;
                }
                // 发送信息
                if(flag) {
                    return data;
                }
            }
        }
        // 关闭流
        reader.close();
        // 如果出错，抛出异常
        if (printErrorMsg){
            Matcher m = Pattern.compile("\"message\": \"(.*?)\"").matcher(errMsg.toString());
            if (m.find()){
                log.error("请求报错："+m.group(1),openAiRequest);
                throw new DefineException(RespCode.ERROR);
            }
            throw new DefineException(RespCode.REQUEST_OPENAI_ERROR);
        }

        return "未知错误";
    }

    private Object imageQuiz(OpenAiRequest openAiDto, String apikey) throws IOException {
        // 请求参数
        Map<String, Object> params = MapUtil.ofEntries(
                MapUtil.entry("prompt", openAiDto.getContent()),
                MapUtil.entry("size", "256x256")
        );
        // 调用接口
        String result = HttpRequest.post(openAiConfig.getImageApi())
                .header(Header.CONTENT_TYPE, "application/json")
                .header(Header.AUTHORIZATION, "Bearer " + apikey)
                .body(JSONUtil.toJsonStr(params))
                .execute().body();
        // 正则匹配出结果
        Pattern p = Pattern.compile("\"url\": \"(.*?)\"");
        Matcher m = p.matcher(result);
        if (m.find()){
//            webSocketServer.sendMessage(m.group(1));
        } else {
//            webSocketServer.sendMessage("图片生成失败！");
        }
        return null;
    }

}