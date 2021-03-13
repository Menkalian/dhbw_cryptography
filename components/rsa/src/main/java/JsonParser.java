import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

// Implements a VERY simplified JSON Parser (probably too complex for this project, but I will implement it anyway)
public class JsonParser {
    // These methods are not directly needed for this project, but according to SOLID I split them so each method has a single responsibility.
    public JsonObject parse(File inputFile) throws IOException {
        return parse(new FileInputStream(inputFile));
    }

    public JsonObject parse(InputStream inputStream) throws IOException {
        return parse(new String(inputStream.readAllBytes()));
    }

    public JsonObject parse(String input) {
        // Strip all unneeded whitespaces
        input = transformInput(input);

        JsonObject toReturn = new JsonObject();

        // We only need to parse Objects so test for curly braces only (otherwise we would need to look for [ as well)
        if (input.startsWith("{") && input.endsWith("}")) {
            input = input.substring(1, input.length() - 1);
        } else {
            throw new RuntimeException("Json is invalid: No Object delimiters found");
        }

        while (input.length() > 0) {
            String currentPair;
            int endOfValue = input.indexOf(',');

            if (endOfValue != -1) {
                currentPair = input.substring(0, endOfValue);
                input = input.substring(endOfValue + 1);
            } else {
                currentPair = input;
                input = "";
            }

            // Parse currentPair
            int posOfSeparator = currentPair.indexOf(":");
            String key = currentPair.substring(0, posOfSeparator);
            String value = currentPair.substring(posOfSeparator + 1);

            if (key.startsWith("\"") && key.endsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            } else {
                throw new RuntimeException("Json is invalid: Attributes should be written as Strings");
            }

            toReturn.put(key, parseValue(value));
        }

        return toReturn;
    }

    private Object parseValue(String valueString) {
        if (valueString.startsWith("\"") && valueString.endsWith("\"")) {
            return valueString.substring(1, valueString.length() - 1);
        }

        try {
            return Integer.parseInt(valueString);
        } catch (NumberFormatException ex) {
            // ignore
        }

        try {
            return Double.parseDouble(valueString);
        } catch (NumberFormatException ex) {
            // ignore
        }

        // This if is neccessary since Java implements parseBoolean kinda strange.
        if (valueString.equals("true") || valueString.equals("false"))
            return Boolean.parseBoolean(valueString);

        if (valueString.startsWith("{")) {
            return parse(valueString);
        }

        // Return null by default
        return null;
    }

    private String transformInput(String input) {
        StringBuilder strippedInput = new StringBuilder();

        // Only strip whitespaces outside of String literals
        String[] temporarySplit = input.split("[\"]");
        for (int i = 0 ; i < temporarySplit.length ; i++) {
            // Uneven indicies are String tokens
            if (i % 2 == 1) {
                strippedInput.append("\"").append(temporarySplit[i]).append("\"");
            } else {
                // Remove whitespaces outside of strings since we do not need them
                strippedInput.append(temporarySplit[i].replaceAll("\\s", ""));
            }
        }

        return strippedInput.toString();
    }

    public static class JsonObject extends HashMap<String, Object> {
        public String getString(String fieldName) {
            return (String) get(fieldName);
        }

        public int getInt(String fieldName) {
            return (int) get(fieldName);
        }

        public JsonObject getObject(String fieldName) {
            return (JsonObject) get(fieldName);
        }
    }
}
