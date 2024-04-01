package RapidWeb.server;

import RapidWeb.http.HttpRequest;
import RapidWeb.http.HttpResponse;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.Date;
import java.util.function.BiConsumer;

import static java.lang.System.out;

class RouteHandler implements HttpHandler {

    private final BiConsumer<HttpRequest, HttpResponse> getHandler;
    private final String route;

    public RouteHandler(BiConsumer<HttpRequest, HttpResponse> getHandler, String route) {
        this.getHandler = getHandler;
        this.route = route;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestURI().getPath().equals(route)) {
            handleNotFound(exchange);
        }

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            handlePostRequest(exchange);
        } else {
            handleGetRequest(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        Headers header = exchange.getRequestHeaders();

        var httpRequest = new HttpRequest(
                header
        );

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
        StringBuilder requestBody = new StringBuilder();
        InputStream input = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        reader.close();

        // Now you have the request body in requestBody
        String responseData = "Received POST request with data: " + requestBody;

        exchange.sendResponseHeaders(200, responseData.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseData.getBytes());
        os.close();
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
}