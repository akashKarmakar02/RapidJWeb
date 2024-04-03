package RapidWeb.http;

import com.sun.net.httpserver.Headers;

import java.util.Map;

public class HttpRequest {

    private Map<String, Object> body;

    public HttpRequest(Map<String, Object> body) {
        this.body = body;
    }

    public HttpRequest() {}

    public Map<String, Object> getBody() {
        return body;
    }
}
