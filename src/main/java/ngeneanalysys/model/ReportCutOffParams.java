package ngeneanalysys.model;

import java.math.BigDecimal;

public class ReportCutOffParams {
    private String scope;
    private BigDecimal minAlleleFrequency;
    private Integer minReadDepth;
    private Integer minAlternateCount;
    private String populationFrequencyDBs;
    private BigDecimal populationFrequency;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public BigDecimal getMinAlleleFrequency() {
        return minAlleleFrequency;
    }

    public void setMinAlleleFrequency(BigDecimal minAlleleFrequency) {
        this.minAlleleFrequency = minAlleleFrequency;
    }

    public Integer getMinReadDepth() {
        return minReadDepth;
    }

    public void setMinReadDepth(Integer minReadDepth) {
        this.minReadDepth = minReadDepth;
    }

    public Integer getMinAlternateCount() {
        return minAlternateCount;
    }

    public void setMinAlternateCount(Integer minAlternateCount) {
        this.minAlternateCount = minAlternateCount;
    }

    public String getPopulationFrequencyDBs() {
        return populationFrequencyDBs;
    }

    public void setPopulationFrequencyDBs(String populationFrequencyDBs) {
        this.populationFrequencyDBs = populationFrequencyDBs;
    }

    public BigDecimal getPopulationFrequency() {
        return populationFrequency;
    }

    public void setPopulationFrequency(BigDecimal populationFrequency) {
        this.populationFrequency = populationFrequency;
    }

    @Override
    public String toString() {
        return "ReportCutOffParams{" +
                "scope='" + scope + '\'' +
                ", minAlleleFrequency=" + minAlleleFrequency +
                ", minReadDepth=" + minReadDepth +
                ", minAlternateCount=" + minAlternateCount +
                ", populationFrequencyDBs='" + populationFrequencyDBs + '\'' +
                ", populationFrequency=" + populationFrequency +
                '}';
    }
}
