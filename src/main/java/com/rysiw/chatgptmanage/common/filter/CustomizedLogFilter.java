package com.rysiw.chatgptmanage.common.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

import java.util.List;
import java.util.Objects;

public class CustomizedLogFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        List<Marker> markerList = iLoggingEvent.getMarkerList();
        if(Objects.nonNull(markerList)) {
            for (Marker marker : markerList) {
                if ("ELK".equals(marker.getName()))
                    return FilterReply.ACCEPT;
            }
        }
        return FilterReply.DENY;
    }
}
