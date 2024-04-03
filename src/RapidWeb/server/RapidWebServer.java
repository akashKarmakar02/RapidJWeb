package RapidWeb.server;

import RapidWeb.http.HttpRequest;
import RapidWeb.http.HttpResponse;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.function.BiConsumer;

import static java.lang.System.out;


interface Callback {
    void function();
}

public class RapidWebServer {
    private final HttpServer server;
    private final int port;
    private final HashMap<String, RouteHandler> routeHandlerMap;

    public RapidWebServer(int port) throws IOException {
        this.routeHandlerMap = new HashMap<>();
        this.server = HttpServer.create(new InetSocketAddress(port), 512);
        this.port = port;
    }

    public RapidWebServer(int port, int backlog) throws IOException {
        this.routeHandlerMap = new HashMap<>();
        this.server = HttpServer.create(new InetSocketAddress(port), backlog);
        this.port = port;
    }

    public void get(String route, BiConsumer<HttpRequest, HttpResponse> handler) {
        if (routeHandlerMap.containsKey(route)) {
            routeHandlerMap.put(route, routeHandlerMap.get(route).get(handler));
        } else {
            routeHandlerMap.put(route, RouteHandler.create(route).get(handler));
        }
    }

    public void post(String route, BiConsumer<HttpRequest, HttpResponse> handler) {
        if (routeHandlerMap.containsKey(route)) {
            routeHandlerMap.put(route, routeHandlerMap.get(route).post(handler));
        } else {
            routeHandlerMap.put(route, RouteHandler.create(route).post(handler));
        }
    }

    public void run(Callback function) {
        routeHandlerMap.keySet().forEach((route) -> {
            server.createContext(route, routeHandlerMap.get(route));
        });
        server.start();
        function.function();
    }

    public void run() {
        run(() -> out.println("Server is listening on: http://localhost:" + this.port));
    }
}

