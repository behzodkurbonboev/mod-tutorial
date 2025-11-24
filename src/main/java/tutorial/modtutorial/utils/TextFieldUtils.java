package tutorial.modtutorial.utils;


public class TextFieldUtils {
    public static boolean isNotEmpty(String text) {
        return text != null && !text.isBlank();
    }

    public static void validateTextFields(String text, int minLength) {
        if (text == null || text.isBlank()) {
            throw new RuntimeException("Required field is missing");
        }

        if (text.length() < minLength) {
            throw new RuntimeException("Text field should have at least " + minLength + " characters");
        }
    }
}
