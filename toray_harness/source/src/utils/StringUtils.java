package utils;

public class StringUtils {

    public static String removeSpaceFromString(String target){

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < target.length(); i++) {
            if (!Character.isWhitespace(target.charAt(i))) {
                sb.append(target.charAt(i));
            }
        }
        return sb.toString();

    }

    public static String fullWidthToHalfWidth(String fullWidthString) {
        char[] charArray = fullWidthString.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            // Check if the character is a full-width character
            if (charArray[i] >= 65281 && charArray[i] <= 65374) {
                charArray[i] = (char) (charArray[i] - 65248); // Convert to half-width
            } else if (charArray[i] == 12288) {
                charArray[i] = ' '; // Full-width space to regular space
            }
        }
        return new String(charArray);
    }
}
