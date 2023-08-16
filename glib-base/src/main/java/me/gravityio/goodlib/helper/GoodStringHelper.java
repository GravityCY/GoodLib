package me.gravityio.goodlib.helper;

public class GoodStringHelper {

    public static String capitalize(String str) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;
        for (char ch : str.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                capitalizeNext = true;
                sb.append(ch);
            } else if (capitalizeNext) {
                sb.append(Character.toUpperCase(ch));
                capitalizeNext = false;
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

}
