package ngeneanalysys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ngeneanalysys.util.ConvertUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 분석 결과 변이 목록
 * 
 * @author gjyoo
 * @since 2016. 6. 15. 오후 7:37:59
 */
public class SnpInDel {
	private static final long serialVersionUID = -5728637480602616382L;

	private Integer id;
	private Integer sampleId;
	private String serialNumber;
	private String swPathogenicity;
	private String expertPathogenicity;
	private String swTier;
	private String expertTier;
	private String includedInReport;
	private String hasWarning;
	private String warningReason;
	private Integer variantNum;
	private String comment;
	private String isFalse;
	private String falseReason;

	private SnpInDelExpression snpInDelExpression;
	private DBSNP dbSNP;
	private ClinicalDB clinicalDB;

	private GenomicCoordinate genomicCoordinate;

	private ReadInfo readInfo;

	private PopulationFrequency populationFrequency;

	//private Integer interpretationEvidenceId;
	private String ntChangeBRCA;

	/**
	 * @return isFalse
	 */
	public String getIsFalse() {
		return isFalse;
	}

	/**
	 * @return falseReason
	 */
	public String getFalseReason() {
		return falseReason;
	}

	/**
	 * @return genomicCoordinate
	 */
	public GenomicCoordinate getGenomicCoordinate() {
		return genomicCoordinate;
	}

	/**
	 * @param genomicCoordinate
	 */
	public void setGenomicCoordinate(GenomicCoordinate genomicCoordinate) {
		this.genomicCoordinate = genomicCoordinate;
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

	public DBSNP getDbSNP() {
		return dbSNP;
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
	public ClinicalDB getClinicalDB() {
		return clinicalDB;
	}

	/**
	 * @param clinicalDB
	 */
	public void setClinicalDB(ClinicalDB clinicalDB) {
		this.clinicalDB = clinicalDB;
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

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	/**
	 * @return swPathogenicity
	 */
	public String getSwPathogenicity() {
		return swPathogenicity;
	}

	/**
	 * @param swPathogenicity
	 */
	public void setSwPathogenicity(String swPathogenicity) {
		this.swPathogenicity = swPathogenicity;
	}

	/**
	 * @return expertPathogenicity
	 */
	public String getExpertPathogenicity() {
		return expertPathogenicity;
	}

	/**
	 * @param expertPathogenicity
	 */
	public void setExpertPathogenicity(String expertPathogenicity) {
		this.expertPathogenicity = expertPathogenicity;
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
	 * @return includedInReport
	 */
	public String getIncludedInReport() {
		return includedInReport;
	}

	/**
	 * @param includedInReport
	 */
	public void setIncludedInReport(String includedInReport) {
		this.includedInReport = includedInReport;
	}

	/**
	 * @return hasWarning
	 */
	public String getHasWarning() {
		if("Y".equalsIgnoreCase(this.hasWarning)) {
			return (StringUtils.isNotEmpty(this.warningReason)) ? this.warningReason : "NONE";
		}
		return null;
	}

	/**
	 * @param hasWarning
	 */
	public void setHasWarning(String hasWarning) {
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
	 * @return snpInDelExpression
	 */
	public SnpInDelExpression getSnpInDelExpression() {
		return snpInDelExpression;
	}

	/**
	 * @param snpInDelExpression
	 */
	public void setSnpInDelExpression(SnpInDelExpression snpInDelExpression) {
		this.snpInDelExpression = snpInDelExpression;
	}

	/**
	 * @return the cDNAbic
	 */
	public String getNtChangeBRCA() {
		return ConvertUtil.insertTextAtFixedPosition(ntChangeBRCA, 15, "\n");
	}

	public void setNtChangeBRCA() {
		this.ntChangeBRCA = createNtChangeBRCA();
	}

	public String createNtChangeBRCA() {
		String cDNAbic = this.getSnpInDelExpression().getNtChange();
		String gene = getGenomicCoordinate().getGene().toUpperCase();
		if (StringUtils.isNotEmpty(cDNAbic)
				&& getGenomicCoordinate() != null && getGenomicCoordinate().getGene() != null
				&& (gene.equals("BRCA1") || gene.equals("BRCA2"))) {
			List<String> findCDNANums = new ArrayList<>();
			Pattern p = Pattern.compile("\\d+");
			Matcher m;
			if(cDNAbic.contains(":")) {
				String tempcDNAbic = cDNAbic.substring(cDNAbic.indexOf(':') + 1);
				m = p.matcher(tempcDNAbic);
			} else {
				m = p.matcher(cDNAbic);
			}
			while (m.find()) {
				findCDNANums.add(m.group());
			}
			for(String cdnaItem : findCDNANums){
				try {
					int cdnaNum = Integer.parseInt(cdnaItem);
					if(gene.equals("BRCA1")){
						cDNAbic = cDNAbic.replace(cdnaItem, String.valueOf(cdnaNum+119));
					} else if (gene.equals("BRCA2")){
						cDNAbic = cDNAbic.replace(cdnaItem, String.valueOf(cdnaNum+228));
					}
				} catch (NumberFormatException e){
					return "cDNA parsing error. " + cdnaItem;
				}
			}
			return cDNAbic;
		}
		return "";
	}

	@Override
	public String
	toString() {
		return "SnpInDel{" +
				"id=" + id +
				", sampleId=" + sampleId +
				", serialNumber='" + serialNumber + '\'' +
				", swPathogenicity='" + swPathogenicity + '\'' +
				", expertPathogenicity='" + expertPathogenicity + '\'' +
				", swTier='" + swTier + '\'' +
				", expertTier='" + expertTier + '\'' +
				", includedInReport='" + includedInReport + '\'' +
				", hasWarning='" + hasWarning + '\'' +
				", warningReason='" + warningReason + '\'' +
				", variantNum=" + variantNum +
				", comment='" + comment + '\'' +
				", snpInDelExpression=" + snpInDelExpression +
				", clinicalDB=" + clinicalDB +
				", genomicCoordinate=" + genomicCoordinate +
				", readInfo=" + readInfo +
				", populationFrequency=" + populationFrequency +
				'}';
	}
}
