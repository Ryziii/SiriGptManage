package com.rysiw.chatgptmanage.controller.ai;

import com.rysiw.chatgptmanage.common.annotation.RateLimit;
import com.rysiw.chatgptmanage.common.vo.ResultVO;
import com.rysiw.chatgptmanage.gptmanager.common.dto.ChatCompletionRequestDTO;
import com.rysiw.chatgptmanage.gptmanager.common.dto.OpenAiRequest;
import com.rysiw.chatgptmanage.gptmanager.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Jie
 * @date 2018-11-24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openai")
public class OpenAiController {

    private final OpenAiService openAiService;

    /**
     * 查询OpenAi余额
     * @return
     */
    @PostMapping("/credit/")
    public ResponseEntity<Object> creditQuery() {
        return new ResponseEntity<>(openAiService.creditQuery(), HttpStatus.OK);
    }

    @PostMapping("/myQuery")
    @RateLimit(key = "myQuery", permitsPerSecond = 1, timeout = 1300, msg = "您的操作太频繁了，喝口水后再来试试吧~")
    public ChatCompletionResult myQuery(@RequestBody ChatCompletionRequestDTO openAiDto) {
        return openAiService.myQuery(openAiDto);
    }
//    @PostMapping("/myQuery")
//    @ResponseBody
//    public String myQuery(ChatCompletionRequest request) {
//        try {
//            // 获取请求输入流
//            InputStream inputStream = request.getInputStream();
//            // 读取请求输入流中的内容
//            byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);
//            String requestBody = new String(bytes, request.getCharacterEncoding());
//            // 输出请求体
//            System.out.println(requestBody);
//            return "success";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "fail";
//        }
//    }
}