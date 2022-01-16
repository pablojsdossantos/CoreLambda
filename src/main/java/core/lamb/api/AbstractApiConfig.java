package core.lamb.api;

import java.nio.charset.Charset;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author Pablo JS dos Santos
 */
@ApplicationScoped
public class AbstractApiConfig {
    private long maxRequestSize;
    private Charset inputEncoding;
    private Charset outputEncoding;
    private String sessionIdFieldName;

    public AbstractApiConfig(
        @ConfigProperty(name = "api.maxRequestSize", defaultValue = "8192") String maxRequestSize,
        @ConfigProperty(name = "api.inputEncoding", defaultValue = "UTF-8") String inputEncoding,
        @ConfigProperty(name = "api.outputEncoding", defaultValue = "UTF-8") String outputEncoding,
        @ConfigProperty(name = "api.sessionIdFieldName", defaultValue = "api-key") String sessionIdFieldName) {
        this.maxRequestSize = Long.parseLong(maxRequestSize);
        this.inputEncoding = Charset.forName(inputEncoding);
        this.outputEncoding = Charset.forName(outputEncoding);
        this.sessionIdFieldName = sessionIdFieldName;
    }

    public long getMaxRequestSize() {
        return maxRequestSize;
    }

    public Charset getInputEncoding() {
        return inputEncoding;
    }

    public Charset getOutputEncoding() {
        return outputEncoding;
    }

    public String getSessionIdFieldName() {
        return sessionIdFieldName;
    }
}
