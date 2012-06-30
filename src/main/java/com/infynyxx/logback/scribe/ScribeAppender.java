package com.infynyxx.logback.scribe;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.AppenderBase;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import scribe.thrift.scribe.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class ScribeAppender<E> extends AppenderBase<E> {

    // The following are configurable via logback configuration
    private String facility = "scribe";
    private String scribeHost = "127.0.0.1";
    private int scribePort = 1463;
    private String category = "logback";
    private Map<String, String> additionalFields = new HashMap<String, String>();

    // hidden fields (not configurable)
    private AppenderExecutor<E> appenderExecutor;

    private TFramedTransport transport = null;

    private void initialize() {

        try {
            TSocket socket = new TSocket(scribeHost, scribePort);
            transport = new TFramedTransport(socket);
            TBinaryProtocol protocol = new TBinaryProtocol(transport, false, false);
            Client client = new Client(protocol, protocol);

            transport.open();
            ScribeConverter scribeConverter = new ScribeConverter(facility, additionalFields, scribeHost);
            appenderExecutor = new AppenderExecutor<E>(client, scribeConverter, category);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing appender appenderExecutor", e);
        }
    }

    @Override
    protected void append(E eventObject) {
        try {
            appenderExecutor.append(eventObject);
        } catch (RuntimeException e) {
            this.addError("Error occurred: ", e);
            throw e;
        }
    }

    @Override
    public void start() {
        super.start();
        initialize();
    }

    @Override
    public void stop() {
        super.stop();
        if (transport != null) {
            transport.close();
        }
    }

    //////////////////////////
    // GETTERS and SETTERS //
    /////////////////////////

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getScribeHost() {
        return scribeHost;
    }

    public void setScribeHost(String scribeHost) {
        this.scribeHost = scribeHost;
    }

    public int getScribePort() {
        return scribePort;
    }

    public void setScribePort(int port) {
        this.scribePort = port;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
    }

    public void addAdditionalField(String keyValue) {
        String[] splitted = keyValue.split(":");

        if (splitted.length != 2) {

            throw new IllegalArgumentException("additionalField must be of the format key:value, where key is the MDC "
                    + "key, and value is the scribe field name. But found '" + keyValue + "' instead.");
        }

        additionalFields.put(splitted[0], splitted[1]);
    }

    private String getStringStackTrace(Exception e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        return result.toString();
    }
}
