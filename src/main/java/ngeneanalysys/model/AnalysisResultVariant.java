package ngeneanalysys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import ngeneanalysys.code.enums.ACMGFilterCode;
import ngeneanalysys.util.JsonUtil;

/**
 * 분석 결과 변이 목록
 * 
 * @author gjyoo
 * @since 2016. 6. 15. 오후 7:37:59
 */
public class AnalysisResultVariant implements Serializable {
	private static final long serialVersionUID = -5728637480602616382L;

	private Integer id;
	private Integer sampleId;
	private String swPathogenicityLevel;
	private String expertPathogenicityLevel;
	private String swTier;
	private String expertTier;
	private Integer skipReport;
	private Integer hasWarning;
	private String warningReason;
	private Integer variantNum;
	private String comment;

	private VariantExpression variantExpression;

	private ClinicalSignificant clinicalSignificant;

	private SequenceInfo sequenceInfo;

	private ReadInfo readInfo;

	private PopulationFrequency populationFrequency;

	/**
	 * @return sequenceInfo
	 */
	public SequenceInfo getSequenceInfo() {
		return sequenceInfo;
	}

	/**
	 * @param sequenceInfo
	 */
	public void setSequenceInfo(SequenceInfo sequenceInfo) {
		this.sequenceInfo = sequenceInfo;
	}

	/**
	 * @return readInfo
	 */
	public ReadInfo getReadInfo() {
		return readInfo;
	}

	/**
	 * @param readInfo
	 */
	public void setReadInfo(ReadInfo readInfo) {
		this.readInfo = readInfo;
	}

	/**
	 * @return populationFrequency
	 */
	public PopulationFrequency getPopulationFrequency() {
		return populationFrequency;
	}

	/**
	 * @param populationFrequency
	 */
	public void setPopulationFrequency(PopulationFrequency populationFrequency) {
		this.populationFrequency = populationFrequency;
	}

	/**
	 * @return clinicalSignificant
	 */
	public ClinicalSignificant getClinicalSignificant() {
		return clinicalSignificant;
	}

	/**
	 * @param clinicalSignificant
	 */
	public void setClinicalSignificant(ClinicalSignificant clinicalSignificant) {
		this.clinicalSignificant = clinicalSignificant;
	}

	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return sampleId
	 */
	public Integer getSampleId() {
		return sampleId;
	}

	/**
	 * @param sampleId
	 */
	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}

	/**
	 * @return swPathogenicityLevel
	 */
	public String getSwPathogenicityLevel() {
		return swPathogenicityLevel;
	}

	/**
	 * @param swPathogenicityLevel
	 */
	public void setSwPathogenicityLevel(String swPathogenicityLevel) {
		this.swPathogenicityLevel = swPathogenicityLevel;
	}

	/**
	 * @return expertPathogenicityLevel
	 */
	public String getExpertPathogenicityLevel() {
		return expertPathogenicityLevel;
	}

	/**
	 * @param expertPathogenicityLevel
	 */
	public void setExpertPathogenicityLevel(String expertPathogenicityLevel) {
		this.expertPathogenicityLevel = expertPathogenicityLevel;
	}

	/**
	 * @return swTier
	 */
	public String getSwTier() {
		return swTier;
	}

	/**
	 * @param swTier
	 */
	public void setSwTier(String swTier) {
		this.swTier = swTier;
	}

	/**
	 * @return expertTier
	 */
	public String getExpertTier() {
		return expertTier;
	}

	/**
	 * @param expertTier
	 */
	public void setExpertTier(String expertTier) {
		this.expertTier = expertTier;
	}

	/**
	 * @return skipReport
	 */
	public Integer getSkipReport() {
		return skipReport;
	}

	/**
	 * @param skipReport
	 */
	public void setSkipReport(Integer skipReport) {
		this.skipReport = skipReport;
	}

	/**
	 * @return hasWarning
	 */
	public Integer getHasWarning() {
		return hasWarning;
	}

	/**
	 * @param hasWarning
	 */
	public void setHasWarning(Integer hasWarning) {
		this.hasWarning = hasWarning;
	}

	/**
	 * @return warningReason
	 */
	public String getWarningReason() {
		return warningReason;
	}

	/**
	 * @param warningReason
	 */
	public void setWarningReason(String warningReason) {
		this.warningReason = warningReason;
	}

	/**
	 * @return variantNum
	 */
	public Integer getVariantNum() {
		return variantNum;
	}

	/**
	 * @param variantNum
	 */
	public void setVariantNum(Integer variantNum) {
		this.variantNum = variantNum;
	}

	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return variantExpression
	 */
	public VariantExpression getVariantExpression() {
		return variantExpression;
	}

	/**
	 * @param variantExpression
	 */
	public void setVariantExpression(VariantExpression variantExpression) {
		this.variantExpression = variantExpression;
	}

	/** variant system unique id *//*
	@JsonProperty("id")
	private Integer id;
	*//** 샘플 고유 아이디 *//*
	@JsonProperty("sample")
	private String sampleId;
	*//** prediction *//*
	@JsonProperty("prediction")
	private String prediction;
	*//** pathogenic *//*
	@JsonProperty("pat")
	private String pathogenic;
	*//** Variant ID *//*
	@JsonProperty("variant_id")
	private String variantId;
	*//** type *//*
	@JsonProperty("type")
	private String type;
	*//** transcript > coding consequence *//*
	@JsonProperty("coding_consequence")
	private String codingConsequence;
	*//** transcript > refseqid *//*
	@JsonProperty("refseqid")
	private String refSeqId;
	*//** transcript > c.DNA *//*
	@JsonProperty("c_dna")
	private String cDNA;
	*//** transcript > protein *//*
	@JsonProperty("protein")
	private String protein;
	*//** transcript > exon id *//*
	@JsonProperty("exon_id")
	private String exonId;
	*//** transcript > coding consequence *//*
	@JsonProperty("gene_strand")
	private String geneStrand;
	*//** exon id for BIC*//*
	@JsonProperty("exon_id_bic")
	private String exonIdBIC;
	*//** gene *//*
	@JsonProperty("gene")
	private String gene;
	*//** ref *//*
	@JsonProperty("ref")
	private String ref;
	*//** alt *//*
	@JsonProperty("alt")
	private String alt;
	*//** ref 이전 좌측 염기서열 문자열 *//*
	@JsonProperty("left_seq")
	private String leftSeq;
	*//** ref 다음 우측 염기서열 문자열 *//*
	@JsonProperty("right_seq")
	private String rightSeq;
	*//** chromosome *//*
	@JsonProperty("chromosome")
	private String chromosome;
	*//** snp *//*
	@JsonProperty("snp")
	private String snp;
	*//** variant_depth *//*
	@JsonProperty("variant_depth")
	private String variantDepth;
	*//** allele_fraction *//*
	@JsonProperty("allele_fraction")
	private String alleleFraction;
	*//** reference_number *//*
	@JsonProperty("reference_number")
	private String referenceNumber;
	*//** alternate_number *//*
	@JsonProperty("alternate_number")
	private String alternateNumber;
	*//** genomic_position *//*
	@JsonProperty("genomic_position")
	private String genomicPosition;
	*//** genomic_end_position *//*
	@JsonProperty("genomic_end_position")
	private String genomicEndPosition;
	*//** reference_genome *//*
	@JsonProperty("reference_genome")
	private String referenceGenome;
	*//** warning *//*
	@JsonProperty("warning")
	private String warning;
	*//** sift *//*
	@JsonProperty("sift")
	private String sift;
	*//** polyphen2 *//*
	@JsonProperty("polyphen2")
	private String polyphen2;
	@JsonProperty("mutationtaster")
	private String mutationtaster;
	*//** esp5400 *//*
	@JsonProperty("esp_6500")
	private String esp6500;
	*//** exac_east_asian *//*
	@JsonProperty("exac")
	private String exac;
	*//** g1000_genomes *//*
	@JsonProperty("g1000_genomes")
	private String thousandGenomics;
	*//** clinical *//*
	@JsonProperty("clinical")
	private String clinical;
	*//** bic_category *//*
	@JsonProperty("bic_category")
	private String bicCategory;
	*//** bic_importance *//*
	@JsonProperty("bic_importance")
	private String bicImportance;
	*//** bic_classification *//*
	@JsonProperty("bic_classification")
	private String bicClassification;
	*//** bic_designation *//*
	@JsonProperty("bic_designation")
	private String bicDesignation;
	*//** bic_NT *//*
	@JsonProperty("bic_nt")
	private String bicNT;
	*//** kohbra_patient *//*
	@JsonProperty("kohbra_patient")
	private String kohbraPatient;
	*//** kohbra_frequency *//*
	@JsonProperty("kohbra_frequency")
	private String kohbraFrequency;
	*//** korean exome *//*
	@JsonProperty("korean_exome")
	private String koreanExome;
	*//** serial number *//*
	@JsonProperty("serial_number")
	private String serialNumber;
	*//** warning flag 이유 *//*
	@JsonProperty("warning_reason")
	private String warningReason;
	*//** experiment type *//*
	@JsonProperty("experiment_type")
	private String experimentType;
	*//** Flagging > Reported Y/N *//*
	@JsonProperty("pathogenic_report_yn")
	private String pathogenicReportYn;
	*//** Flagging > False Y/N *//*
	@JsonProperty("pathogenic_false_yn")
	private String pathogenicFalseYn;

	*//** enigma *//*
	@JsonProperty("enigma")
	private String enigma;
	*//** be.clin.update	BRCA_Exchange>Date_last_updated_ClinVar *//*
	@JsonProperty("be_clin_update")
	private String beClinUpdate;
	*//** be.clin.origin	BRCA_Exchange>Allele_Origin_ClinVar	 *//*
	@JsonProperty("be_clin_origin")
	private String beClinOrigin;
	*//** be.clin.meth	BRCA_Exchange>Analysis_Method_ClinVar *//*
	@JsonProperty("be_clin_meth")
	private String beClinMeth;
	*//** be.bic.cate	BRCA_Exchange>Mutation_category_BIC *//*
	@JsonProperty("be_bic_cate")
	private String beBicCate;
	*//** be.bic.eth	BRCA_Exchange>Ethnicity_BIC *//*
	@JsonProperty("be_bic_eth")
	private String beBicEth;
	*//** be.bic.nat	BRCA_Exchange>Patient_nationality_BIC *//*
	@JsonProperty("be_bic_nat")
	private String beBicNat;
	*//** be.ref	BRCA_Exchange>Reference_cDNA_Sequence *//*
	@JsonProperty("be_ref")
	private String beRef;
	*//** be.nuc	BRCA_Exchange>Nucleotide *//*
	@JsonProperty("be_nuc")
	private String beNuc;
	*//** be.gene	BRCA_Exchange>Gene_Symbol *//*
	@JsonProperty("be_gene")
	private String beGene;
	*//** be.eni_cond	BRCA_Exchange>Condition_ID_Type_ENIGMA *//*
	@JsonProperty("be_eni_cond")
	private String beEniCond;
	*//** be.eni.update	BRCA_Exchange>Date_last_evaluated_ENIGMA *//*
	@JsonProperty("be_eni_update")
	private String beEniUpdate;
	*//** be.eni.comm	BRCA_Exchange>Comment_on_Clinical_significance_ENIGMA *//*
	@JsonProperty("be_eni_comm")
	private String beEniComm;
	*//** be.path.clin	BRCA_Exchange>Pathogenicity>ClinVar *//*
	@JsonProperty("be_path_clin")
	private String bePathClin;
	*//** be.path.eni	BRCA_Exchange>Pathogenicity>ENIGMA *//*
	@JsonProperty("be_path_eni")
	private String bePathEni;
	*//** be.path.bic	BRCA_Exchange>Pathogenicity>BIC *//*
	@JsonProperty("be_path_bic")
	private String bePathBic;

	*//** zygosity *//*
	@JsonProperty("zygosity")
	private String zygosity;
	@JsonIgnore
	private String cDNAbic;

	*//** Clinical Information *//*
	@JsonIgnore
	private VariantClinical variantClinical;

	*//**
	 * warning flag가 yes 인 경우 Warning Reason 값 반환
	 * @return
	 *//*
	public String getWarningReasonIfWarningIsYes() {
		if("YES".equals(this.warning.toUpperCase())) {
			return (!StringUtils.isEmpty(this.warningReason)) ? this.warningReason : "NONE";
		}
		return null;
	}

	*//**
	 * @return the id
	 *//*
	public Integer getId() {
		return id;
	}
	*//**
	 * @param id the id to set
	 *//*
	public void setId(Integer id) {
		this.id = id;
	}
	*//**
	 * @return the sampleId
	 *//*
	public String getSampleId() {
		return sampleId;
	}
	*//**
	 * @param sampleId the sampleId to set
	 *//*
	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}
	*//**
	 * @return the prediction
	 *//*
	public String getPrediction() {
		return prediction;
	}
	*//**
	 * @param prediction the prediction to set
	 *//*
	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}
	*//**
	 * @return the pathogenic
	 *//*
	public String getPathogenic() {
		return pathogenic;
	}
	*//**
	 * @param pathogenic the pathogenic to set
	 *//*
	public void setPathogenic(String pathogenic) {
		this.pathogenic = pathogenic;
	}

	public String getPathogenicAlias() {
		String value = null;
		if (this.pathogenic != null) {
			if (this.pathogenic.equals(ACMGFilterCode.PREDICTION_A.getCode())) {
				value = ACMGFilterCode.PREDICTION_A.getAlias();
			} else if (this.pathogenic.equals(ACMGFilterCode.PREDICTION_B.getCode())) {
				value = ACMGFilterCode.PREDICTION_B.getAlias();
			} else if (this.pathogenic.equals(ACMGFilterCode.PREDICTION_C.getCode())) {
				value = ACMGFilterCode.PREDICTION_C.getAlias();
			} else if (this.pathogenic.equals(ACMGFilterCode.PREDICTION_D.getCode())) {
				value = ACMGFilterCode.PREDICTION_D.getAlias();
			} else if (this.pathogenic.equals(ACMGFilterCode.PREDICTION_E.getCode())) {
				value = ACMGFilterCode.PREDICTION_E.getAlias();
			}
		}
		return value;

	}
	*//**
	 * @return the variantId
	 *//*
	public String getVariantId() {
		return variantId;
	}
	*//**
	 * @param variantId the variantId to set
	 *//*
	public void setVariantId(String variantId) {
		this.variantId = variantId;
	}
	*//**
	 * @return the type
	 *//*
	public String getType() {
		return type;
	}
	*//**
	 * @param type the type to set
	 *//*
	public void setType(String type) {
		this.type = type;
	}
	*//**
	 * @return the gene
	 *//*
	public String getGene() {
		return gene;
	}
	*//**
	 * @param gene the gene to set
	 *//*
	public void setGene(String gene) {
		this.gene = gene;
	}
	*//**
	 * @return the ref
	 *//*
	public String getRef() {
		return ref;
	}
	*//**
	 * @param ref the ref to set
	 *//*
	public void setRef(String ref) {
		this.ref = ref;
	}
	*//**
	 * @return the alt
	 *//*
	public String getAlt() {
		return alt;
	}
	*//**
	 * @param alt the alt to set
	 *//*
	public void setAlt(String alt) {
		this.alt = alt;
	}
	*//**
	 * @return the leftSeq
	 *//*
	public String getLeftSeq() {
		return leftSeq;
	}
	*//**
	 * @param leftSeq the leftSeq to set
	 *//*
	public void setLeftSeq(String leftSeq) {
		this.leftSeq = leftSeq;
	}
	*//**
	 * @return the rightSeq
	 *//*
	public String getRightSeq() {
		return rightSeq;
	}
	*//**
	 * @param rightSeq the rightSeq to set
	 *//*
	public void setRightSeq(String rightSeq) {
		this.rightSeq = rightSeq;
	}
	*//**
	 * @return the chromosome
	 *//*
	public String getChromosome() {
		return chromosome;
	}
	*//**
	 * @param chromosome the chromosome to set
	 *//*
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	*//**
	 * @return the snp
	 *//*
	public String getSnp() {
		return snp;
	}
	*//**
	 * @param snp the snp to set
	 *//*
	public void setSnp(String snp) {
		this.snp = snp;
	}
	*//**
	 * @return the variantDepth
	 *//*
	public String getVariantDepth() {
		return variantDepth;
	}
	*//**
	 * @param variantDepth the variantDepth to set
	 *//*
	public void setVariantDepth(String variantDepth) {
		this.variantDepth = variantDepth;
	}
	*//**
	 * @return the alleleFraction
	 *//*
	public String getAlleleFraction() {
		return alleleFraction;
	}
	*//**
	 * @param alleleFraction the alleleFraction to set
	 *//*
	public void setAlleleFraction(String alleleFraction) {
		this.alleleFraction = alleleFraction;
	}
	*//**
	 * @return the referenceNumber
	 *//*
	public String getReferenceNumber() {
		return referenceNumber;
	}
	*//**
	 * @param referenceNumber the referenceNumber to set
	 *//*
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	*//**
	 * @return the alternateNumber
	 *//*
	public String getAlternateNumber() {
		return alternateNumber;
	}
	*//**
	 * @param alternateNumber the alternateNumber to set
	 *//*
	public void setAlternateNumber(String alternateNumber) {
		this.alternateNumber = alternateNumber;
	}
	*//**
	 * @return the genomicPosition
	 *//*
	public String getGenomicPosition() {
		return genomicPosition;
	}
	*//**
	 * @param genomicPosition the genomicPosition to set
	 *//*
	public void setGenomicPosition(String genomicPosition) {
		this.genomicPosition = genomicPosition;
	}
	*//**
	 * @return the genomicEndPosition
	 *//*
	public String getGenomicEndPosition() {
		return genomicEndPosition;
	}
	*//**
	 * @param genomicEndPosition the genomicEndPosition to set
	 *//*
	public void setGenomicEndPosition(String genomicEndPosition) {
		this.genomicEndPosition = genomicEndPosition;
	}
	*//**
	 * @return the referenceGenome
	 *//*
	public String getReferenceGenome() {
		return referenceGenome;
	}
	*//**
	 * @param referenceGenome the referenceGenome to set
	 *//*
	public void setReferenceGenome(String referenceGenome) {
		this.referenceGenome = referenceGenome;
	}
	*//**
	 * @return the warning
	 *//*
	public String getWarning() {
		return warning;
	}
	*//**
	 * @param warning the warning to set
	 *//*
	public void setWarning(String warning) {
		this.warning = warning;
	}
	*//**
	 * @return the sift
	 *//*
	public String getSift() {
		return sift;
	}
	*//**
	 * @param sift the sift to set
	 *//*
	public void setSift(String sift) {
		this.sift = sift;
	}
	*//**
	 * @return the polyphen2
	 *//*
	public String getPolyphen2() {
		return polyphen2;
	}
	*//**
	 * @param polyphen2 the polyphen2 to set
	 *//*
	public void setPolyphen2(String polyphen2) {
		this.polyphen2 = polyphen2;
	}
	*//**
	 * @return the mutationtaster
	 *//*
	public String getMutationtaster() {
		return mutationtaster;
	}
	*//**
	 * @param mutationtaster the mutationtaster to set
	 *//*
	public void setMutationtaster(String mutationtaster) {
		this.mutationtaster = mutationtaster;
	}
	*//**
	 * @return the esp6500
	 *//*
	public String getEsp6500() {
		return esp6500;
	}
	*//**
	 * @param esp6500 the esp6500 to set
	 *//*
	public void setEsp6500(String esp6500) {
		this.esp6500 = esp6500;
	}
	*//**
	 * @return the exacEastAsian
	 *//*
	public String getExac() {
		return exac;
	}
	*//**
	 * @param exac EastAsian the exacEastAsian to set
	 *//*
	public void setExac(String exac) {
		this.exac = exac;
	}
	*//**
	 * @return the thousandGenomics
	 *//*
	public String getThousandGenomics() {
		return thousandGenomics;
	}
	*//**
	 * @param thousandGenomics the thousandGenomics to set
	 *//*
	public void setThousandGenomics(String thousandGenomics) {
		this.thousandGenomics = thousandGenomics;
	}
	*//**
	 * @return the clinical
	 *//*
	public String getClinical() {
		return clinical;
	}
	*//**
	 * @param clinical the clinical to set
	 *//*
	public void setClinical(String clinical) {
		this.clinical = clinical;
		if(!StringUtils.isEmpty(this.clinical)) {
			VariantClinical variantClinical1 = new VariantClinical();
			Map<String, Object> clinicalMap = JsonUtil.fromJson(this.clinical.replaceAll("'", "\""),
					new HashMap<String, Object>().getClass());
			if (clinicalMap.containsKey("clinvar_accession")){
				variantClinical1.setClinvarAccession((String)clinicalMap.get("clinvar_accession"));
			}
			if (clinicalMap.containsKey("hgvs")){
				variantClinical1.setHgvs((String)clinicalMap.get("hgvs"));
			}
			if (clinicalMap.containsKey("classification")){
				variantClinical1.setClassification((String)clinicalMap.get("classification"));
			}
			if (clinicalMap.containsKey("radar")){
				variantClinical1.setRadar((String)clinicalMap.get("radar"));
			}
			if (clinicalMap.containsKey("variant_disease_db_name")){
				variantClinical1.setVariantDiseaseDbName((String)clinicalMap.get("variant_disease_db_name"));
			}
			setVariantClinical(variantClinical1);
		}
	}
	*//**
	 * @return the bicCategory
	 *//*
	public String getBicCategory() {
		return bicCategory;
	}
	*//**
	 * @param bicCategory the bicCategory to set
	 *//*
	public void setBicCategory(String bicCategory) {
		this.bicCategory = bicCategory;
	}
	*//**
	 * @return the bicImportance
	 *//*
	public String getBicImportance() {
		return bicImportance;
	}
	*//**
	 * @param bicImportance the bicImportance to set
	 *//*
	public void setBicImportance(String bicImportance) {
		this.bicImportance = bicImportance;
	}
	*//**
	 * @return the bicClassification
	 *//*
	public String getBicClassification() {
		return bicClassification;
	}
	*//**
	 * @param bicClassification the bicClassification to set
	 *//*
	public void setBicClassification(String bicClassification) {
		this.bicClassification = bicClassification;
	}
	*//**
	 * @return the bicDesignation
	 *//*
	public String getBicDesignation() {
		return bicDesignation;
	}

	*//**
	 * @param bicDesignation the bicDesignation to set
	 *//*
	public void setBicDesignation(String bicDesignation) {
		this.bicDesignation = bicDesignation;
	}

	*//**
	 * @return the bicNT
	 *//*
	public String getBicNT() {
		return bicNT;
	}

	*//**
	 * @param bicNT the bicNT to set
	 *//*
	public void setBicNT(String bicNT) {
		this.bicNT = bicNT;
	}

	*//**
	 * @return the kohbraPatient
	 *//*
	public String getKohbraPatient() {
		return kohbraPatient;
	}
	*//**
	 * @param kohbraPatient the kohbraPatient to set
	 *//*
	public void setKohbraPatient(String kohbraPatient) {
		this.kohbraPatient = kohbraPatient;
	}
	*//**
	 * @return the kohbraFrequency
	 *//*
	public String getKohbraFrequency() {
		return kohbraFrequency;
	}
	*//**
	 * @param kohbraFrequency the kohbraFrequency to set
	 *//*
	public void setKohbraFrequency(String kohbraFrequency) {
		this.kohbraFrequency = kohbraFrequency;
	}
	*//**
	 * @return the variantClinical
	 *//*
	public VariantClinical getVariantClinical() {
		return variantClinical;
	}
	*//**
	 * @param variantClinical the variantClinical to set
	 *//*
	public void setVariantClinical(VariantClinical variantClinical) {
		this.variantClinical = variantClinical;
	}
	*//**
	 * @return the koreanExome
	 *//*
	public String getKoreanExome() {
		return koreanExome;
	}
	*//**
	 * @param koreanExome the koreanExome to set
	 *//*
	public void setKoreanExome(String koreanExome) {
		this.koreanExome = koreanExome;
	}
	*//**
	 * @return the serialNumber
	 *//*
	public String getSerialNumber() {
		return serialNumber;
	}
	*//**
	 * @param serialNumber the serialNumber to set
	 *//*
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	*//**
	 * @return the warningReason
	 *//*
	public String getWarningReason() {
		return warningReason;
	}
	*//**
	 * @param warningReason the warningReason to set
	 *//*
	public void setWarningReason(String warningReason) {
		this.warningReason = warningReason;
	}

	*//**
	 * @return the codingConsequence
	 *//*
	public String getCodingConsequence() {
		return codingConsequence;
	}

	*//**
	 * @param codingConsequence the codingConsequence to set
	 *//*
	public void setCodingConsequence(String codingConsequence) {
		this.codingConsequence = codingConsequence;
	}

	*//**
	 * @return the refSeqId
	 *//*
	public String getRefSeqId() {
		return refSeqId;
	}

	*//**
	 * @param refSeqId the refSeqId to set
	 *//*
	public void setRefSeqId(String refSeqId) {
		this.refSeqId = refSeqId;
	}

	*//**
	 * @return the cDNA
	 *//*
	public String getcDNA() {
		return cDNA;
	}

	*//**
	 * @param cDNA the cDNA to set
	 *//*
	public void setcDNA(String cDNA) {
		this.cDNA = cDNA;
	}

	*//**
	 * @return the cDNAbic
	 *//*
	public String getcDNAbic() {
		this.cDNAbic = this.cDNA;
		if (this.cDNAbic != null
				&& !this.cDNAbic.isEmpty()
				&& this.gene != null
				&& (this.gene.toUpperCase().equals("BRCA1") || this.gene.toUpperCase().equals("BRCA2"))) {
			List<String> findCDNANums = new ArrayList<String>();
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(cDNAbic);
			while (m.find()) {
				findCDNANums.add(m.group());
			}
			int cdnaNum = 0;
			for(String cdnaItem : findCDNANums){
				try {
					cdnaNum = Integer.parseInt(cdnaItem);
					if(this.gene.toUpperCase().equals("BRCA1")){
						this.cDNAbic = this.cDNAbic.replace(cdnaItem, String.valueOf(cdnaNum+119));
					} else if (this.gene.toUpperCase().equals("BRCA2")){
						this.cDNAbic = this.cDNAbic.replace(cdnaItem, String.valueOf(cdnaNum+228));
					} else {

					}
				} catch (NumberFormatException e){
					return "cDNA parsing error. " + cdnaItem;
				}
			}
			return this.cDNAbic;
		}
		return "";
	}

	*//**
	 * @param cDNAbic the cDNAbic to set
	 *//*
	public void setcDNAbic(String cDNAbic) {
		this.cDNAbic = cDNAbic;
	}

	*//**
	 * @return the protein
	 *//*
	public String getProtein() {
		return protein;
	}

	*//**
	 * @param protein the protein to set
	 *//*
	public void setProtein(String protein) {
		this.protein = protein;
	}

	*//**
	 * @return the exonId
	 *//*
	public String getExonId() {
		return exonId;
	}

	*//**
	 * @param exonId the exonId to set
	 *//*
	public void setExonId(String exonId) {
		this.exonId = exonId;
	}

	*//**
	 * @return the exonIdBIC
	 *//*
	public String getExonIdBIC() {
		return exonIdBIC;
	}

	*//**
	 * @param exonIdBIC the exonIdBIC to set
	 *//*
	public void setExonIdBIC(String exonIdBIC) {
		this.exonIdBIC = exonIdBIC;
	}

	*//**
	 * @return the geneStrand
	 *//*
	public String getGeneStrand() {
		return geneStrand;
	}

	*//**
	 * @param geneStrand the geneStrand to set
	 *//*
	public void setGeneStrand(String geneStrand) {
		this.geneStrand = geneStrand;
	}

	*//**
	 * @return the experimentType
	 *//*
	public String getExperimentType() {
		return experimentType;
	}

	*//**
	 * @param experimentType the experimentType to set
	 *//*
	public void setExperimentType(String experimentType) {
		this.experimentType = experimentType;
	}

	*//**
	 * @return the pathogenicReportYn
	 *//*
	public String getPathogenicReportYn() {
		return pathogenicReportYn;
	}

	*//**
	 * @param pathogenicReportYn the pathogenicReportYn to set
	 *//*
	public void setPathogenicReportYn(String pathogenicReportYn) {
		this.pathogenicReportYn = pathogenicReportYn;
	}

	*//**
	 * @return the pathogenicFalseYn
	 *//*
	public String getPathogenicFalseYn() {
		return pathogenicFalseYn;
	}

	*//**
	 * @param pathogenicFalseYn the pathogenicFalseYn to set
	 *//*
	public void setPathogenicFalseYn(String pathogenicFalseYn) {
		this.pathogenicFalseYn = pathogenicFalseYn;
	}

	*//**
	 * @return the enigma
	 *//*
	public String getEnigma() {
		return enigma;
	}

	*//**
	 * @param enigma the enigma to set
	 *//*
	public void setEnigma(String enigma) {
		this.enigma = enigma;
	}

	*//**
	 * @return the beClinUpdate
	 *//*
	public String getBeClinUpdate() {
		return beClinUpdate;
	}

	*//**
	 * @param beClinUpdate the beClinUpdate to set
	 *//*
	public void setBeClinUpdate(String beClinUpdate) {
		this.beClinUpdate = beClinUpdate;
	}

	*//**
	 * @return the beClinOrigin
	 *//*
	public String getBeClinOrigin() {
		return beClinOrigin;
	}

	*//**
	 * @param beClinOrigin the beClinOrigin to set
	 *//*
	public void setBeClinOrigin(String beClinOrigin) {
		this.beClinOrigin = beClinOrigin;
	}

	*//**
	 * @return the beClinMeth
	 *//*
	public String getBeClinMeth() {
		return beClinMeth;
	}

	*//**
	 * @param beClinMeth the beClinMeth to set
	 *//*
	public void setBeClinMeth(String beClinMeth) {
		this.beClinMeth = beClinMeth;
	}

	*//**
	 * @return the beBicCate
	 *//*
	public String getBeBicCate() {
		return beBicCate;
	}

	*//**
	 * @param beBicCate the beBicCate to set
	 *//*
	public void setBeBicCate(String beBicCate) {
		this.beBicCate = beBicCate;
	}

	*//**
	 * @return the beBicEth
	 *//*
	public String getBeBicEth() {
		return beBicEth;
	}

	*//**
	 * @param beBicEth the beBicEth to set
	 *//*
	public void setBeBicEth(String beBicEth) {
		this.beBicEth = beBicEth;
	}

	*//**
	 * @return the beBicNat
	 *//*
	public String getBeBicNat() {
		return beBicNat;
	}

	*//**
	 * @param beBicNat the beBicNat to set
	 *//*
	public void setBeBicNat(String beBicNat) {
		this.beBicNat = beBicNat;
	}

	*//**
	 * @return the beRef
	 *//*
	public String getBeRef() {
		return beRef;
	}

	*//**
	 * @param beRef the beRef to set
	 *//*
	public void setBeRef(String beRef) {
		this.beRef = beRef;
	}

	*//**
	 * @return the beNuc
	 *//*
	public String getBeNuc() {
		return beNuc;
	}

	*//**
	 * @param beNuc the beNuc to set
	 *//*
	public void setBeNuc(String beNuc) {
		this.beNuc = beNuc;
	}

	*//**
	 * @return the beGene
	 *//*
	public String getBeGene() {
		return beGene;
	}

	*//**
	 * @param beGene the beGene to set
	 *//*
	public void setBeGene(String beGene) {
		this.beGene = beGene;
	}

	*//**
	 * @return the beEniCond
	 *//*
	public String getBeEniCond() {
		return beEniCond;
	}

	*//**
	 * @param beEniCond the beEniCond to set
	 *//*
	public void setBeEniCond(String beEniCond) {
		this.beEniCond = beEniCond;
	}

	*//**
	 * @return the beEniUpdate
	 *//*
	public String getBeEniUpdate() {
		return beEniUpdate;
	}

	*//**
	 * @param beEniUpdate the beEniUpdate to set
	 *//*
	public void setBeEniUpdate(String beEniUpdate) {
		this.beEniUpdate = beEniUpdate;
	}

	*//**
	 * @return the beEniComm
	 *//*
	public String getBeEniComm() {
		return beEniComm;
	}

	*//**
	 * @param beEniComm the beEniComm to set
	 *//*
	public void setBeEniComm(String beEniComm) {
		this.beEniComm = beEniComm;
	}

	*//**
	 * @return the bePathClin
	 *//*
	public String getBePathClin() {
		return bePathClin;
	}

	*//**
	 * @param bePathClin the bePathClin to set
	 *//*
	public void setBePathClin(String bePathClin) {
		this.bePathClin = bePathClin;
	}

	*//**
	 * @return the bePathEni
	 *//*
	public String getBePathEni() {
		return bePathEni;
	}

	*//**
	 * @param bePathEni the bePathEni to set
	 *//*
	public void setBePathEni(String bePathEni) {
		this.bePathEni = bePathEni;
	}

	*//**
	 * @return the bePathBic
	 *//*
	public String getBePathBic() {
		return bePathBic;
	}

	*//**
	 * @param bePathBic the bePathBic to set
	 *//*
	public void setBePathBic(String bePathBic) {
		this.bePathBic = bePathBic;
	}

	*//**
	 * @return the zygosity
	 *//*
	public String getZygosity() {
		return zygosity;
	}

	*//**
	 * @param zygosity the zygosity to set
	 *//*
	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}*/
}
