package ngeneanalysys.model;

import ngeneanalysys.model.FusionGene;
import ngeneanalysys.model.VariantAndInterpretationEvidence;

/**
 * @author Jang
 * @since 2018-01-22
 */
public class FusionGeneView {
    private FusionGene fusionGene;
    private VariantAndInterpretationEvidence interpretation;

    /**
     * @return fusionGene
     */
    public FusionGene getFusionGene() {
        return fusionGene;
    }

    /**
     * @return interpretation
     */
    public VariantAndInterpretationEvidence getInterpretation() {
        return interpretation;
    }
}
