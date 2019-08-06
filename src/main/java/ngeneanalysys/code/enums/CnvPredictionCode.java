package ngeneanalysys.code.enums;

import ngeneanalysys.util.StringUtils;

/**
 * @author Jang
 * @since 2019-03-04
 */
public enum CnvPredictionCode {
    DELETION("deletion", "D"),
    DUPLICATION("duplication", "A");

    private String code;
    private String initial;

    CnvPredictionCode(String code, String initial) {
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
        }
        return "";
    }
}
