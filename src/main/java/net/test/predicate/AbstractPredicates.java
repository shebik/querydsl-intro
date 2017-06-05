package net.test.predicate;

/**
 * @author Zbynek Vavros (zbynek.vavros@i.cz)
 */
public abstract class AbstractPredicates {

    private static final String WILDCARD_MULTIPLE_CHARS = "%";

    public static String like(String value) {
        return like(false, value);
    }

    public static String like(boolean leftLike, String value) {
        StringBuilder result = new StringBuilder();

        if (leftLike) {
            result.append(WILDCARD_MULTIPLE_CHARS);
        }

        result.append(value).append(WILDCARD_MULTIPLE_CHARS);

        return result.toString();
    }
}
