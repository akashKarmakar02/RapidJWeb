package RapidJWeb.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DjangoTemplating {

    public String parse(String template, Object data) {

        List<String> variables = extractVariables(template);

            template = renderConditionalBlocks(template, data);

        for (String variable : variables) {
            Object value = getValueFromObject(data, variable);
            template = template.replace("{{ " + variable + " }}", value.toString());
        }

        return template;
    }

    private Object getValueFromObject(Object data, String variable) {
        try {
            var field = data.getClass().getDeclaredField(variable);
            field.setAccessible(true);
            return field.get(data);
        } catch (Exception e) {
            return "null";
        }
    }

    private List<String> extractVariables(String template) {
        List<String> variables = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\{\\s*(.*?)\\s*}}");
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String variable = matcher.group(1);
            variables.add(variable.trim());
        }

        return variables;
    }

    public String renderConditionalBlocks(String htmlContent, Object data) {
        Pattern pattern = Pattern.compile("\\{%\\s*if\\s+(\\w+)\\s*(>=|<=|>|<|==)\\s*(?:\"(\\w+)\"|([^\"\\s]+))\\s*%}(.*?)(?:\\{%\\s*else\\s*%}(.*?))?\\{%\\s*endif\\s*%}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(htmlContent);
        StringBuilder result = new StringBuilder();

        // Iterate through matches and evaluate conditions
        while (matcher.find()) {
            String variable = matcher.group(1);
            String operator = matcher.group(2);
            String valueString = matcher.group(3);
            Optional<Integer> valueInt = Optional.empty();
            if (valueString == null) {
                valueInt = Optional.of(Integer.parseInt(matcher.group(4)));
            } else {
                valueString = "\"" + valueString + "\"";
            }
            String ifContent = matcher.group(5);
            String elseContent = matcher.group(6);

            var variableValue = getValueFromObject(data, variable);

            boolean toRender;

            if (valueInt.isEmpty()) {
                toRender = evaluateCondition(operator, valueString, variableValue);
            } else {
                toRender = evaluateCondition(operator, valueInt.get(), variableValue);
            }

            if (toRender) {
                matcher.appendReplacement(result, ifContent);
            } else {
                matcher.appendReplacement(result, elseContent);
            }
        }

        matcher.appendTail(result);

        return result.toString();
    }



    public static boolean evaluateCondition(String operator, Object value1, Object value2) {
        if (value1.getClass() != value2.getClass()) {
            return false;
        }
        return switch (operator) {
            case ">" -> compareValues(value1, value2) > 0;
            case ">=" -> compareValues(value1, value2) >= 0;
            case "<" -> compareValues(value1, value2) < 0;
            case "<=" -> compareValues(value1, value2) <= 0;
            case "==" -> compareValues(value1, value2) == 0;
            default -> false;
        };
    }

    public static int compareValues(Object value1, Object value2) {
        if (value1 instanceof Integer && value2 instanceof Integer) {
            return Integer.compare((Integer) value1, (Integer) value2);
        } else if (value1 instanceof String && value2 instanceof String) {
            return ((String) value1).compareTo((String) value2);
        } else {
            throw new IllegalArgumentException("Unsupported data types for comparison");
        }
    }
}
