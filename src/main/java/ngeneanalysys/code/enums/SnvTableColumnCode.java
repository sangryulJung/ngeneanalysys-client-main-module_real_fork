package ngeneanalysys.code.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-10-31
 */
public enum SnvTableColumnCode {
    PATHOGENICITY("Pathogenicity", "pathogenicity"),
    TIER("Tier", "tier"),
    WARNING("Warning", "warningReason"),
    FALSE("False", "falseReason"),
    REPORT("Report", "includedInReport"),
    GENE("Gene", "gene"),
    TRANSCRIPT_ACCESSION("Transcript Accession", "transcriptAccession"),
    PROTEIN_ACCESSION("Protein Accession", "proteinAccession"),
    TYPE("Type", "variantType"),
    CONSEQUENCE("Consequence", "codingConsequence"),
    NT_CHANGE("NT Change", "ntChange"),
    NT_CHANGE_BIC("NT Change (BIC)", "ntChangeBic"),
    AA_CHANGE("AA Change", "aaChange"),
    AA_CHANGE_SINGLE("AA Change (Single)", "aaChangeSingleLetter"),
    CHROMOSOME("Chr", "chromosome"),
    START_POSITION("Start Position", "startPosition"),
    REF("Ref", "refSequence"),
    ALT("Alt", "altSequence"),
    FRACTION("Fraction", "alleleFraction"),
    DEPTH("Depth", "readDepth"),
    REF_COUNT("Ref Count", "refReadNum"),
    ALT_COUNT("Alt Count", "altReadNum"),
    DBSNP_ID("dbSNP ID", "dbSnpRsId"),
    EXON("Exon", "exonNum"),
    COSMIC_ID("COSMIC ID", "cosmicIds"),
    CLINVAR_SUBMITTED_ACCESSION("ClinVar Submitted Accession", "clinVarSubmittedAcc"),
    CLINVAR_SUBMITTED_CLASS("ClinVar Submitted Class", "clinVarSubmittedClass"),
    CLINVAR_REVIEW_STATUS("ClinVar Review Status", "clinVarReviewStatus"),
    CLINVAR_SUBMITTED_DISEASE("ClinVar Submitted Disease", "clinVarSubmittedDisease"),
    CLINVAR_CONDITION("ClinVar Condition", "clinVarDrugResponse"),
    CLINVAR_SUBMITTED_OMIM("ClinVar Submitted OMIM", "clinVarSubmittedOMIM"),
    KGP_ALL("1KGP All", "g1000All"),
    KGP_AFR("1KGP AFR", "g1000African"),
    KGP_AMR("1KGP AMR", "g1000American"),
    KGP_EAS("1KGP EAS", "g1000EastAsian"),
    KGP_EUR("1KGP EUR", "g1000European"),
    KGP_SAS("1KGP SAS", "g1000SouthAsian"),
    ESP_ALL("ESP All", "esp6500All"),
    ESP_AA("ESP AA", "esp6500aa"),
    ESP_EA("ESP EA", "esp6500ea"),
    EXAC("ExAC", "exac"),
    GNOMAD_ALL("gnomAD All","gnomADall"),
    GNOMAD_AMR("gnomAD AMR", "gnomADadmixedAmerican"),
    GNOMAD_AFR("gnomAD AFR", "gnomADafricanAfricanAmerican"),
    GNOMAD_EAS("gnomAD EAS", "gnomADeastAsian"),
    GNOMAD_FIN("gnomAD FIN", "gnomADfinnish"),
    GNOMAD_NFE("gnomAD NFE", "gnomADnonFinnishEuropean"),
    GNOMAD_OTH("gnomAD OTH", "gnomADothers"),
    GNOMAD_SAS("gnomAD SAS", "gnomADsouthAsian"),
    KRGDB("KRGDB", "koreanReferenceGenomeDatabase"),
    KOEXID("KoEXID", "koreanExomInformationDatabase"),
    DBSNP_COMMON_ID("dbSNP Common ID", "dbSnpCommonId"),
    STRAND("Strand", "strand"),
    TYPE_EXTENSION("Type Extension", "variantTypeExtension"),
    EXON_BIC("Exon (BIC)", "exonNumBic"),
    ZYGOSITY("Zygosity", "zygosity"),
    SIFT_PREDICTION("SIFT Prediction", "siftPrediction"),
    MUTATION_TASTER_PREDICTION("Mutation Taster Prediction", "mutationTasterPrediction"),
    GERP_NR("GERP++ NR", "gerpNrScore"),
    GERP_RS("GERP++ RS", "gerpRsScore"),
    FATHMM_PREDICTION("FATHMM Prediction", "fathmmPrediction"),
    LRT_PREDICTION("LRT Prediction", "lrtPrediction"),
    MUTATION_ASSESSOR_PREDICTION("Mutation Assessor Prediction", "mutationAssessorPrediction"),
    COSMIC_OCCURRENCE("COSMIC Occurrence", "cosmicOccurrence"),
    COSMIC_COUNT("COSMIC Count", "cosmicCount"),
    BIC_CATEGORY("BIC Category", "bicCategory"),
    BIC_CLASS("BIC Class", "bicClass"),
    BIC_DESIGNATION("BIC Designation", "bicDesignation"),
    BIC_IMPORTANCE("BIC Importance", "bicImportance"),
    BIC_NT("BIC NT", "bicNt"),
    KOHBRA_FREQUENCY("KOHBRA Frequency", "kohbraFreq"),
    KOHBRA_PATIENT("KOHBRA Patient", "kohbraPatient"),
    BE_BIC_CATEGORY("Be BIC Category", "beBicCategory"),
    BE_BIC_ETHNIC("Be BIC Ethnic", "beBicEthnic"),
    BE_BIC_NATIONALITY("Be BIC Nationality", "beBicNationality"),
    BE_BIC_PATHOGENICITY("Be BIC Pathogenicity", "beBicPathogenicity"),
    BE_CLINVAR_METHOD("Be ClinVar Method", "beClinVarMethod"),
    BE_CLINVAR_ORIGIN("Be ClinVar Origin", "beClinVarOrigin"),
    BE_CLINVAR_PATHOGENICITY("Be ClinVar Pathogenicity", "beClinVarPathogenicity"),
    BE_CLINVAR_UPDATE("Be ClinVar Update", "beClinVarUpdate"),
    BE_ENIGMA_CONDITION("Be ENIGMA Condition", "beEnigmaCondition"),
    BE_ENIGMA_PATHOGENICITY("Be ENIGMA Pathogenicity", "beEnigmaPathogenicity"),
    BE_ENIGMA_UPDATE("Be ENIGMA Update", "beEnigmaUpdate"),
    BE_GENE("Be Gene", "beGene"),
    BE_NT("Be NT", "beNt"),
    BE_TRANSCRIPT("Be Transcript", "beTranscript"),
    ENIGMA("ENIGMA", "enigma"),
    CLINVAR_VARIATION_ID("ClinVar Variation ID", "clinVarVariationId"),
    CLINVAR_INTERPRETATION("ClinVar Interpretation", "clinVarInterpretation"),
    METASVM_PREDICTION("MetaSVM Prediction", "metaSvmPrediction"),
    DBSCSNV_ADA_SCORE("dbscSNV ADA Score", "dbscSnvAdaScore"),
    DBSCSNV_RF_SCORE("dbscSNV RF Score", "dbscSnvRfScore"),
    INHERITANCE("Inheritance", "inheritance"),
    COMMON_VARIANTS("Common Variants", "commonVariants"),
    CUSTOM_DATABASE("Custom Database", "customDatabase");

    private String name;
    private String id;

    SnvTableColumnCode(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public static String getIdFromName(String name) {
        Optional<SnvTableColumnCode> column = Arrays.stream(SnvTableColumnCode.values())
                .filter(v -> v.name.equals(name)).findFirst();
        return column.map(snvTableColumnCode -> snvTableColumnCode.id).orElse(null);
    }
}