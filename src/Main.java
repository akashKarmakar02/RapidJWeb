import RapidJWeb.server.RapidWebServer;

import java.io.IOException;


class Main {
    public static void main(String[] args) throws IOException {
        var server = new RapidWebServer(8080);

        server.get("/", (request, response) -> {
            var person = new Person("Akash", 21);
            response.render("index", person);
        });

        server.post("/", ((request, response) -> {
            response.setResponse("Hello " + request.getBody().get("name"));
        }));

        server.get("/about", ((request, response) -> {
            var person = new Person("Ananya", 3);
            response.render("about", person);
        }));

        server.run();
    }
}
