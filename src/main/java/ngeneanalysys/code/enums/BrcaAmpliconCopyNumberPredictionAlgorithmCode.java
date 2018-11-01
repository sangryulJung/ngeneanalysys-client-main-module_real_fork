package ngeneanalysys.code.enums;

import ngeneanalysys.util.StringUtils;

/**
 * @author Jang
 * @since 2018-10-31
 */
public enum BrcaAmpliconCopyNumberPredictionAlgorithmCode {
    DISTRIBUTION("Distribution"),
    SIMPLE_CUTOFF("Simple Cut-off");

    private String code;

    BrcaAmpliconCopyNumberPredictionAlgorithmCode(String code) {
        this.code = code;
    }

    /**
     * @return code
     */
    public String getCode() {
        return code;
    }
}
