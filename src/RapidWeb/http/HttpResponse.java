package RapidWeb.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class HttpResponse {

    private String response;
    private int status;

    public void render(String template) {
        String filePath = template + ".html";

        try {
            Path path = Paths.get("src/templates/" + filePath);
            var list = extractVariables(Files.readString(path));
            out.println(list);
            this.response = Files.readString(path);
        } catch (IOException e) {
            this.response = "<h1>Template name is invalid " + filePath + " </h1>";
            out.println(e.getMessage());
        }
    }


    private List<String> extractVariables(String template) {
        List<String> variables = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\{\\s*(.*?)\\s*\\}\\}");
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String variable = matcher.group(1);
            variables.add(variable.trim());
        }

        return variables;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public HttpResponse status(int status) {
        this.status = status;
        return this;
    }

    public String getResponse() {
        return response;
    }

    public int getStatus() {
        return status;
    }
}
