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
}
