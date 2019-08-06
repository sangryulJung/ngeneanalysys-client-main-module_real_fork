package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2019-05-29
 */
public class ExonMeanCoverage {
    private Integer id;
    private Integer sampleId;
    private String gene;
    private String transcript;
    private Integer exon;
    private BigDecimal coverage;
    private String chromosome;
    private Integer startPosition;
    private Integer endPosition;

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
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @return transcript
     */
    public String getTranscript() {
        return transcript;
    }

    /**
     * @return exon
     */
    public Integer getExon() {
        return exon;
    }

    /**
     * @return coverage
     */
    public BigDecimal getCoverage() {
        return coverage;
    }

    /**
     * @return chromosome
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * @return startPosition
     */
    public Integer getStartPosition() {
        return startPosition;
    }

    /**
     * @return endPosition
     */
    public Integer getEndPosition() {
        return endPosition;
    }
}
