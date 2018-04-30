package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-01-12
 */
public class ExonSkip {
    private Integer id;
    private Integer sampleId;
    private Integer exonNumber;
    private Integer totalReadCount;
    private Integer skippedReadCount;
    private BigDecimal skippedRate;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return exonNumber
     */
    public Integer getExonNumber() {
        return exonNumber;
    }

    /**
     * @return totalReadCount
     */
    public Integer getTotalReadCount() {
        return totalReadCount;
    }

    /**
     * @return skippedReadCount
     */
    public Integer getSkippedReadCount() {
        return skippedReadCount;
    }

    /**
     * @return skippedRate
     */
    public BigDecimal getSkippedRate() {
        return skippedRate;
    }
}
