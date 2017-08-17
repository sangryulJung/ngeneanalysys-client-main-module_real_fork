package ngeneanalysys.util;

public class StringUtils {
	
	private StringUtils() {
		throw new IllegalAccessError("StringUtils");
	}
	
	public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

}
