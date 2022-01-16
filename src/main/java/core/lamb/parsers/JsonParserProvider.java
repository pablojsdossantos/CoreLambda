package core.lamb.parsers;

import io.quarkus.arc.DefaultBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 *
 * @author Pablo JS dos Santos
 */
@ApplicationScoped
public class JsonParserProvider {
    private JsonParser parser;

    public JsonParserProvider() {
        this.parser = new GsonJsonParser();
    }

    @Produces
    @DefaultBean
    public JsonParser getJsonParser() {
        return this.parser;
    }
}
