package com.infynyxx.logback.scribe;

import org.apache.thrift.TException;
import scribe.thrift.LogEntry;
import scribe.thrift.ResultCode;
import scribe.thrift.scribe.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class AppenderExecutor<E> {

    private final Client client;
    private final ScribeConverter converter;
    private final String category;

    private List<LogEntry> logEntries = new ArrayList<LogEntry>();

    public AppenderExecutor(Client client, ScribeConverter converter, String category) {
        this.client = client;
        this.converter = converter;
        this.category = category;
    }

    public void append(E logEvent) {
        logEntries.add(new LogEntry(category, converter.getMessage(logEvent)));
        try {
            ResultCode resultCode = client.Log(logEntries);
            if (resultCode == ResultCode.OK) {
                logEntries.clear();
            }
        } catch (TException e) {
            System.err.println(e.getMessage());
        }
    }
}
