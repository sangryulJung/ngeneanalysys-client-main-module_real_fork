package ngeneanalysys.code.enums;

import ngeneanalysys.util.StringUtils;

/**
 * @author Jang
 * @since 2018-10-31
 */
public enum BrcaCNVCode {
    DELETION("deletion", "D"),
    DUPLICATION("duplication", "A"),
    NORMAL("normal", "N");

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
        if(StringUtils.isEmpty(code)) {
            return "";
        } else if(code.equals(DELETION.code)) {
            return DELETION.initial;
        } else if(code.equals(DUPLICATION.code)) {
            return DUPLICATION.initial;
        } else if(code.equals(NORMAL.code)) {
            return NORMAL.initial;
        }
        return "";
    }
}
