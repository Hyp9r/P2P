package mreze.p2p.util;

public class NumberUtils {

    public static Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException e) {
            return null;
        }
    }
}
