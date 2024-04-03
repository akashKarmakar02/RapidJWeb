package RapidJWeb.template;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DjangoTemplating {

    public String parse(String template, Object data) {

        List<String> variables = extractVariables(template);

        for (String variable : variables) {
            String value = getValueFromObject(data, variable);
            template = template.replace("{{ " + variable + " }}", value);
        }

        return template;
    }

    private String getValueFromObject(Object data, String variable) {
        try {
            var field = data.getClass().getDeclaredField(variable);
            field.setAccessible(true);
            return field.get(data).toString();
        } catch (Exception e) {
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
}
