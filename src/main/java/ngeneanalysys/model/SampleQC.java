package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2017-10-10
 */
public class SampleQC {
    private Integer sampleId;
    private String qcType;
    private String qcUnit;
    private BigDecimal qcValue;
    private String qcThreshold;
    private String qcResult;
    private String qcDescription;

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return qcType
     */
    public String getQcType() {
        return qcType;
    }

    /**
     * @return qcUnit
     */
    public String getQcUnit() {
        return qcUnit;
    }

    /**
     * @return qcValue
     */
    public BigDecimal getQcValue() {
        return qcValue;
    }

    /**
     * @return qcThreshold
     */
    public String getQcThreshold() {
        return qcThreshold;
    }

    /**
     * @return qcResult
     */
    public String getQcResult() {
        return qcResult;
    }

    /**
     * @return qcDescription
     */
    public String getQcDescription() {
        return qcDescription;
    }
}
