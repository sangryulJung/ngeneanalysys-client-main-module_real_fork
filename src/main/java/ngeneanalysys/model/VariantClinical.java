package ngeneanalysys.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 분석 결과 변이 > Clinical 정보
 * 
 * @author gjyoo
 * @since 2016. 6. 15. 오후 11:55:13
 */
public class VariantClinical implements Serializable {
	private static final long serialVersionUID = 8476265439985991648L;

	@JsonProperty("classification")
	private String classification;
	
	@JsonProperty("clinvar_accession")
	private String clinvarAccession;
	
	@JsonProperty("variant_disease_db_name")
	private String variantDiseaseDbName;
	
	@JsonProperty("variant_disease_name")
	private String variantDiseaseName;
	
	@JsonProperty("hgvs")
	private String hgvs;
	
	@JsonProperty("radar")
	private String radar;

	/**
	 * @return the classification
	 */
	public String getClassification() {
		return classification;
	}
	/**
	 * @param classification the classification to set
	 */
	public void setClassification(String classification) {
		this.classification = classification;
	}
	/**
	 * @return the clinvarAccession
	 */
	public String getClinvarAccession() {
		return clinvarAccession;
	}
	/**
	 * @param clinvarAccession the clinvarAccession to set
	 */
	public void setClinvarAccession(String clinvarAccession) {
		this.clinvarAccession = clinvarAccession;
	}
	/**
	 * @return the variantDiseaseDbName
	 */
	public String getVariantDiseaseDbName() {
		return variantDiseaseDbName;
	}
	/**
	 * @param variantDiseaseDbName the variantDiseaseDbName to set
	 */
	public void setVariantDiseaseDbName(String variantDiseaseDbName) {
		this.variantDiseaseDbName = variantDiseaseDbName;
	}
	/**
	 * @return the variantDiseaseName
	 */
	public String getVariantDiseaseName() {
		return variantDiseaseName;
	}
	/**
	 * @param variantDiseaseName the variantDiseaseName to set
	 */
	public void setVariantDiseaseName(String variantDiseaseName) {
		this.variantDiseaseName = variantDiseaseName;
	}
	/**
	 * @return the hgvs
	 */
	public String getHgvs() {
		return hgvs;
	}
	/**
	 * @param hgvs the hgvs to set
	 */
	public void setHgvs(String hgvs) {
		this.hgvs = hgvs;
	}
	/**
	 * @return the radar
	 */
	public String getRadar() {
		return radar;
	}
	/**
	 * @param radar the radar to set
	 */
	public void setRadar(String radar) {
		this.radar = radar;
	}
}
