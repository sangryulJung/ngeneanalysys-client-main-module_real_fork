package ngeneanalysys.model;

public class TargetROI {
    private Integer id;
    private Integer panelId;
    private String geneSymbol;
    private String chromosome;
    private Integer startPosition;
    private Integer endPosition;
    private String strand;
    private Integer exon;
    private String transcriptId;

    public Integer getId() {
        return id;
    }

    public Integer getPanelId() {
        return panelId;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public String getChromosome() {
        return chromosome;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public String getStrand() {
        return strand;
    }

    public Integer getExon() {
        return exon;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    @Override
    public String toString() {
        return chromosome + '\t' +
                startPosition + '\t' +
                endPosition + '\t' +
                geneSymbol + '\t' +
                strand + '\t' +
                exon + '\t' +
                transcriptId;
    }
}
