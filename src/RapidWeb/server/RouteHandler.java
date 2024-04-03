package RapidWeb.server;

import RapidWeb.http.HttpRequest;
import RapidWeb.http.HttpResponse;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.lang.System.out;

class RouteHandler implements HttpHandler {

    private BiConsumer<HttpRequest, HttpResponse> getHandler;
    private BiConsumer<HttpRequest, HttpResponse> postHandler;
    private final String route;

    private RouteHandler(String route) {
        this.route = route;
    }

    public RouteHandler get(BiConsumer<HttpRequest, HttpResponse> handler) {
        this.getHandler = handler;
        return this;
    }

    public RouteHandler post(BiConsumer<HttpRequest, HttpResponse> handler) {
        this.postHandler = handler;
        return this;
    }

    public static RouteHandler create(String route) {
        return new RouteHandler(route);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestURI().getPath().equals(route)) {
            handleNotFound(exchange);
        }

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            if (postHandler == null) {
                handleMethodNotAllowed(exchange);
            } else {
                handlePostRequest(exchange);
            }
        } else {
            if (getHandler == null) {
                handleMethodNotAllowed(exchange);
            } else {
                handleGetRequest(exchange);
            }
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {

        var httpRequest = new HttpRequest();

        var httpResponse = new HttpResponse();


        getHandler.accept(httpRequest, httpResponse);
        String response = httpResponse.getResponse();
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        out.println(new Date() + " GET: " + exchange.getRequestURI().toString() + " " + exchange.getResponseCode());
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        var inputStream = exchange.getRequestBody();
        Map<String, Object> postData = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                String key = parts[0];
                String value = java.net.URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                postData.put(key, value);
            }
        }
        reader.close();

        var httpRequest = new HttpRequest(postData);

        var httpResponse = new HttpResponse();

        postHandler.accept(httpRequest, httpResponse);

        String response = httpResponse.getResponse();
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        out.println(new Date() + " GET: " + exchange.getRequestURI().toString() + " " + exchange.getResponseCode());
    }

    private void handleNotFound(HttpExchange exchange) throws IOException {
        int statusCode = 404;
        String response = "404 Not Found";
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        out.println(new Date() + " GET: " + exchange.getRequestURI().toString() + " " + exchange.getResponseCode());
    }

    private void handleMethodNotAllowed(HttpExchange exchange) throws IOException {
        int status = 405;
        String response = "405 Method Not Allowed";
        exchange.sendResponseHeaders(status, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}