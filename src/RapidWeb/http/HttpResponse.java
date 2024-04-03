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

    public void render(String template, Object data) {
        String filePath = template + ".html";

        try {
            Path path = Paths.get("src/templates/" + filePath);
            String templateContent = Files.readString(path);

            List<String> variables = extractVariables(templateContent);

            for (String variable : variables) {
                String value = getValueFromObject(data, variable);
                templateContent = templateContent.replace("{{ " + variable + " }}", value);
            }

            // Set the response
            this.response = templateContent;
        } catch (IOException e) {
            this.response = "<h1>Template name is invalid " + filePath + " </h1>";
            out.println(e.getMessage());
        }
    }

    private String getValueFromObject(Object data, String variable) {
        try {
            var field = data.getClass().getDeclaredField(variable);
            out.println(field);
            field.setAccessible(true);
            return field.get(data).toString();
        } catch (Exception e) {
            out.println(e);
            return "null";
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
