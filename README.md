logback-scribe
==============

Simple Logback plugin to send data to Scribe Server

### Using with logback.xml

`TSocket` and `TIOStreamTransport` imports `org.slf4j.Logger` and `org.slf4j.LoggerFactory` so, it's not possible to load them in default `logback.xml` .

So, creating a new file like: `scribe-logback.xml` and loading it explicitly as second step configuration is recommended as mentioned [here](http://www.slf4j.org/codes.html#substituteLogger)

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="scribe" class="com.infynyxx.logback.scribe.ScribeAppender">
        <facility>logback-scribe-test</facility>
        <category>test_category</category>
        <scribeHost>127.0.0.1</scribeHost>
        <scribePort>1463</scribePort>
    </appender>

    <root>
        <level value="debug" />
        <appender-ref ref="scribe" />
    </root>
</configuration>
```

### Example

``` java

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;

public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws TException, JoranException {

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        InputStream in = App.class.getClassLoader().getResourceAsStream("logback-scribe.xml");

        configurator.doConfigure(in);

        logger.debug("hello world");
        logger.debug("hello world2");
    }
}
```