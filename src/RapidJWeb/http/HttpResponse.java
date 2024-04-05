package RapidJWeb.http;

import RapidJWeb.template.DjangoTemplating;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class HttpResponse {

    private String response;
    private int status;

    private final DjangoTemplating templatingEngine;

    public HttpResponse() {
        templatingEngine = new DjangoTemplating();
    }

    public void render(String template, Object data) {
        String filePath = template + ".html";

        try {
            Path path = Paths.get("src/templates/" + filePath);
            String templateContent = Files.readString(path);

            templateContent = templatingEngine.parse(templateContent, data);

            // Set the response
            this.response = templateContent;
        } catch (IOException e) {
            this.response = "<h1>Template name is invalid " + filePath + " </h1>";
        }
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @SuppressWarnings("unused")
    public HttpResponse status(int status) {
        this.status = status;
        return this;
    }

    public String getResponse() {
        return response;
    }

    @SuppressWarnings("unused")
    public int getStatus() {
        return status;
    }
}
