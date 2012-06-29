package com.infynyxx.logback.scribe;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;

import java.util.Map;

/**
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class ScribeConverter<E> {
    private final String facility;
    private final Map<String, String> additionalFields;
    private final String hostName;

    public ScribeConverter(String facility, Map<String, String> additionalFields, String hostName) {
        this.facility = facility;
        this.additionalFields = additionalFields;
        this.hostName = hostName;
    }

    public String getMessage(E logEvent) {
        ILoggingEvent eventObject = (ILoggingEvent) logEvent;

        String message = eventObject.getMessage();

        // Format up the stack trace
        IThrowableProxy proxy = eventObject.getThrowableProxy();

        if (proxy != null) {
            message += "\n" + proxy.getClassName() + ": " + proxy.getMessage() + "\n" + toStackTraceString(proxy.getStackTraceElementProxyArray());
        }

        return message;
    }

    private String toStackTraceString(StackTraceElementProxy[] elements) {
        StringBuilder str = new StringBuilder();
        for (StackTraceElementProxy element : elements) {
            str.append(element.getSTEAsString());
        }
        return str.toString();
    }
}
