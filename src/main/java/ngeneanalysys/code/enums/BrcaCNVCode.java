package ngeneanalysys.code.enums;

import ngeneanalysys.util.StringUtils;

/**
 * @author Jang
 * @since 2018-10-31
 */
public enum BrcaCNVCode {
    COPY_LOSS("Copy Loss", "Loss"),
    COPY_GAIN("Copy Gain", "Gain"),
    NORMAL("Normal", "Nor");

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
        } else if(code.equals(COPY_LOSS.code)) {
            return COPY_LOSS.initial;
        } else if(code.equals(NORMAL.code)) {
            return NORMAL.initial;
        } else if(code.equals(COPY_GAIN.code)) {
            return COPY_GAIN.initial;
        }
        return "";
    }
}
