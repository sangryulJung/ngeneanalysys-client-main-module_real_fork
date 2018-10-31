package ngeneanalysys.code.enums;

/**
 * @author Jang
 * @since 2018-10-31
 */
public enum BrcaCNVCode {
    DELETION("deletion", "Del"),
    DUPLICATION("duplication", "Dup");

    private String code;
    private String initial;

    BrcaCNVCode(String code, String initial) {
        this.code = code;
        this.initial = initial;
    }

    /**
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return initial
     */
    public String getInitial() {
        return initial;
    }

    public static String findInitial(String code) {
        if(code.equals(DELETION.code)) {
            return DELETION.initial;
        } else if(code.equals(DUPLICATION.code)) {
            return DUPLICATION.initial;
        }
        return "";
    }
}
