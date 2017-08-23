package ngeneanalysys.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import ngeneanalysys.MainApp;
import ngeneanalysys.code.enums.PredictionTypeCode;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.AnalysisResultVariant;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.WorksheetUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

/**
 * Variant Data Export Task
 * 
 * @author Joonsik,Jang
 * @since 2017. 2. 13.
 */
public class ExportVariantDataTask extends Task<Void> {
	
	private static Logger logger = LoggerUtil.getLogger();
	private MainApp mainApp;
	private String fileType;
	private File file;
	private List<Map<String, Object>> searchedSamples;
	/** API Service */
	private APIService apiService;
	private boolean createExportFile = false;
	/** Excel, CSV 헤더 배열 */
	private String[] spreadSheetHeaders = new String[] { "Run", "Sample",
			"Prediction", "Pathogenicity", "Warn", "Report", "False", "ID",
			"Type", "Cod.Cons", "Gene", "Strand", "Transcript", "NT change", "AA change", "NT change(BIC)", "ref.genome", 
			"Chr", "Ref", "Alt", "Zygosity", "g.pos", "g.pos.end", "Exon", "Exon(BIC)",
			"ref.num", "alt.num", "depth", "fraction",
			"dbSNP", "1KG", "Exac", "Esp6500", "Korean", 
			"ClinVar.Acc", "ClinVar.disease", "ClinVar.Class", "BIC.Cat", "BIC.Importance", "BIC.Class", "Experiment",
			"BIC.Designation", "BIC.NT", "KOHBRA.patient", "KOHBRA.frequency", "Be.clinvar.update", "Be.clinvar.origin",
			"Be.clinvar.method", "Be.BIC.category", "Be.BIC.nationality", "Be.Bic.ethnic", "Be.Bic.patho", "Be.transcript", "Be.nt", "Be.gene",
			"Be.enigma.condition", "Be.enigma.update", "enigma", "Be.exLOVD.class", "Be.clinvar.patho",
			"Be.enigma.patho", "polyphen2", "sift", "mutationtaster",			
			"Left.seq", "Right.seq" };
	private WebAPIException wae;
	public ExportVariantDataTask(MainApp mainApp, String fileType, File file, List<Map<String, Object>> searchedSamples) {
		this.fileType = fileType;
		this.file = file;
		this.searchedSamples = searchedSamples;
		this.mainApp = mainApp;
		// api service init..
		apiService = (APIService) mainApp.getApplicationContext().getBean("apiService");
		apiService.setStage(mainApp.getPrimaryStage());		
	}

	@Override
	protected Void call() throws Exception {
		updateProgress(0, 1);
		updateMessage("");
		try{			
			if (this.searchedSamples != null && this.searchedSamples.size() > 0) {
				List<String[]> variantData = getSpreadSheetContentsList(searchedSamples);
				if (variantData == null || variantData.size() == 0) {
					return null;
				}
				this.updateMessage("Wrting data to file.");
				if ("Excel".equals(fileType)) {
					logger.info(String.format("start create xlsx..[%s]", file.getName()));
					createExportFile= WorksheetUtil.createXlsxFile(file, "Variants", spreadSheetHeaders, variantData);
				} else {
					logger.info(String.format("start create CSV..[%s]", file.getName()));
					createExportFile= WorksheetUtil.createCSVFile(file, spreadSheetHeaders, variantData);
				}
			} else {
				this.updateMessage("Empty samples");
			}
		} catch (WebAPIException wae) {
			this.wae = wae;			
		} catch (Exception e) {
			e.printStackTrace();
			this.updateMessage("An error occurred during the creation of the " + fileType + " document.");			
		}
		return null;
	}

	/**
	 * 작업 종료시 처리
	 */
	@Override
	protected void done() {
		logger.info("done");
	}
	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#succeeded()
	 */
	@Override
	protected void succeeded() {
		logger.info("success");
		super.succeeded();
		if(this.isCancelled()) {
			return;
		}
		try {
			if (createExportFile) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.initOwner((Window) this.mainApp.getPrimaryStage());
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText("Creating the " + fileType + " document was completed.");
				alert.setContentText("Do you want to check the " + fileType + " document?");							
	
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					this.mainApp.getHostServices().showDocument(file.toURI().toURL().toExternalForm());
				} else {
					alert.close();
				}
			} else if(wae != null) {
				DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), this.mainApp.getPrimaryStage(), false);
			} else {
				DialogUtil.error("Save Fail.",
						"An error occurred during the creation of the " + fileType + " document.\n" + this.getMessage(),
						this.mainApp.getPrimaryStage(), false);
			}
		} catch (Exception e){
			DialogUtil.error("Save Fail.",
					"An error occurred during the creation of the " + fileType + " document.\n" + e.getMessage(),
					this.mainApp.getPrimaryStage(), false);
		}
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#failed()
	 */
	@Override
	protected void failed() {
		logger.info("failed");
		super.failed();
	}
	/**
	 * Spread Sheet Contents Return.
	 * @return
	 * @throws WebAPIException 
	 */
	public List<String[]> getSpreadSheetContentsList(List<Map<String, Object>> searchedSamples) throws WebAPIException {
		
		List<String[]> spreadSheetList = new ArrayList<String[]>();
		if (searchedSamples == null || searchedSamples.size() <= 0 ) {
			return null;
		}
		int completeCount = 0;
		for(Map<String, Object> sample : searchedSamples){
			if (this.isCancelled()){
				return null;
			}
			String sampleID = sample.get("id").toString();
			String sampleName = sample.get("name").toString();
			String runName = sample.get("job_run_group_ref_name").toString();
			// API 서버 조회
			HttpClientResponse response = apiService.get("/analysis_result/variant_list/" + sampleID, null,
					null, false);
			@SuppressWarnings("unchecked")
			List<AnalysisResultVariant> list = (List<AnalysisResultVariant>) response
					.getMultiObjectBeforeConvertResponseToJSON(AnalysisResultVariant.class, false);
			if(list != null && list.size() > 0) {				
				for(AnalysisResultVariant item : list) {
					String[] contents = new String[spreadSheetHeaders.length];
					contents[0] = runName;
					contents[1] = sampleName;
					// Prediction
					PredictionTypeCode prediction = PredictionTypeCode.getByCode(item.getPrediction());
					contents[2] = (prediction != null) ? prediction.getAlias() : "";
					// Pathogenic
					contents[3] = StringUtils.defaultIfEmpty(item.getPathogenicAlias(), "");
					// warning
					contents[4] = StringUtils.defaultIfEmpty(item.getWarning(), "");
					// reported
					contents[5] = StringUtils.defaultIfEmpty(item.getPathogenicReportYn(), "");
					// set flase
					contents[6] = StringUtils.defaultIfEmpty(item.getPathogenicFalseYn(), "");				
					// variant id
					contents[7] = StringUtils.defaultIfEmpty(item.getVariantId(), "");
					// snp type
					contents[8] = StringUtils.defaultIfEmpty(item.getType(), "");
					// coding consequence
					contents[9] = StringUtils.defaultIfEmpty(item.getCodingConsequence(), "");
					// gene symbol
					contents[10] = StringUtils.defaultIfEmpty(item.getGene(), "");
					// gene strand
					contents[11] = StringUtils.defaultIfEmpty(item.getGeneStrand(), "");
					// reference sequence id
					contents[12] = StringUtils.defaultIfEmpty(item.getRefSeqId(), "");
					// c.DNA
					contents[13] = StringUtils.defaultIfEmpty(item.getcDNA(), "");
					// protein
					contents[14] = StringUtils.defaultIfEmpty(item.getProtein(), "");
					// c.DNA BIC
					contents[15] = StringUtils.defaultIfEmpty(item.getcDNAbic(), "");
					// reference genome
					contents[16] = StringUtils.defaultIfEmpty(item.getReferenceGenome(), "");
					// chromosome
					contents[17] = StringUtils.defaultIfEmpty(item.getChromosome(), "");
					// reference
					contents[18] = StringUtils.defaultIfEmpty(item.getRef(), "");
					// alternate
					contents[19] = StringUtils.defaultIfEmpty(item.getAlt(), "");
					// Zygosity
					contents[20] = StringUtils.defaultIfEmpty(item.getZygosity(), "");
					// genomic position
					contents[21] = StringUtils.defaultIfEmpty(item.getGenomicPosition(), "");
					// genomic end position
					contents[22] = StringUtils.defaultIfEmpty(item.getGenomicEndPosition(), "");
					// exon id
					contents[23] = StringUtils.defaultIfEmpty(item.getExonId(), "");
					// exon id bic
					contents[24] = StringUtils.defaultIfEmpty(item.getExonIdBIC(), "");
					// reference number
					contents[25] = StringUtils.defaultIfEmpty(item.getReferenceNumber(), "");
					// alternate number
					contents[26] = StringUtils.defaultIfEmpty(item.getAlternateNumber(), "");
					// variant depth
					contents[27] = StringUtils.defaultIfEmpty(item.getVariantDepth(), "");
					// allele fraction
					contents[28] = String.format("%.2f", Double.parseDouble(StringUtils.defaultIfEmpty(item.getAlleleFraction(), "0")));
					// snp				
					contents[29] = StringUtils.defaultIfEmpty(item.getSnp(), "");
					// 1000genomes allele frequency
					contents[30] = StringUtils.defaultIfEmpty(item.getThousandGenomics(), "");
					// exac allele frequency
					contents[31] = StringUtils.defaultIfEmpty(item.getExac(), "");
					// esp6500 allele frequency
					contents[32] = StringUtils.defaultIfEmpty(item.getEsp6500(), "");
					// Korea Exome allele frequency
					contents[33] = StringUtils.defaultIfEmpty(item.getKoreanExome(), "");
					// clinvar accession
					contents[34] = (item.getVariantClinical() != null) ? StringUtils.defaultIfEmpty(item.getVariantClinical().getClinvarAccession(), "") : "";
					// clinvar disease name
					contents[35] = (item.getVariantClinical() != null) ? StringUtils.defaultIfEmpty(item.getVariantClinical().getVariantDiseaseDbName(), "") : "";
					// clinvar classification
					contents[36] = (item.getVariantClinical() != null) ? StringUtils.defaultIfEmpty(item.getVariantClinical().getClassification(), "") : "";
					// bic category
					contents[37] = StringUtils.defaultIfEmpty(item.getBicCategory(), "");
					// bic importance
					contents[38] = StringUtils.defaultIfEmpty(item.getBicImportance(), "");
					// bic classification
					contents[39] = StringUtils.defaultIfEmpty(item.getBicClassification(), "");
					// Experiment Type
					contents[40] = StringUtils.defaultIfEmpty(item.getExperimentType(), "");
					// BIC Designation
					contents[41] = StringUtils.defaultIfEmpty(item.getBicDesignation(), "");
					// BIC NT
					contents[42] = StringUtils.defaultIfEmpty(item.getBicNT(), "");
					// kohbra patient
					contents[43] = StringUtils.defaultIfEmpty(item.getKohbraPatient(), "");
					// kohbra frequency
					contents[44] = StringUtils.defaultIfEmpty(item.getKohbraFrequency(), "");
					// be.clinvar.update
					contents[45] = StringUtils.defaultIfEmpty(item.getBeClinUpdate(), "");
					// be.clinvar.origin
					contents[46] = StringUtils.defaultIfEmpty(item.getBeClinOrigin(), "");
					// be.clinvar.method
					contents[47] = StringUtils.defaultIfEmpty(item.getBeClinMeth(), "");
					// be.BIC.cate
					contents[48] = StringUtils.defaultIfEmpty(item.getBeBicCate(), "");
					// be.bic.nationality
					contents[49] = StringUtils.defaultIfEmpty(item.getBeBicNat(), "");
					// be.bic.ethnic
					contents[50] = StringUtils.defaultIfEmpty(item.getBeBicEth(), "");
					// be.bic.nationality
					contents[51] = StringUtils.defaultIfEmpty(item.getBePathBic(), "");
					// be.transcript
					contents[52] = StringUtils.defaultIfEmpty(item.getBeRef(), "");
					// be.nt
					contents[53] = StringUtils.defaultIfEmpty(item.getBeNuc(), "");
					// be.gene
					contents[54] = StringUtils.defaultIfEmpty(item.getBeGene(), "");
					// be.enigma.condition
					contents[55] = StringUtils.defaultIfEmpty(item.getBeEniCond(), "");
					// be.enigma.update
					contents[56] = StringUtils.defaultIfEmpty(item.getBeEniUpdate(), "");
					// enigma
					contents[57] = StringUtils.defaultIfEmpty(item.getEnigma(), "");
					// be.exLOVD.class
					contents[58] = StringUtils.defaultIfEmpty(item.getBeEniComm(), "");
					// be.clinvar.patho
					contents[59] = StringUtils.defaultIfEmpty(item.getBePathClin(), "");
					// be.enigma.patho
					contents[60] = StringUtils.defaultIfEmpty(item.getBePathEni(), "");
					// polyphen2
					contents[61] = StringUtils.defaultIfEmpty(item.getPolyphen2(), "");
					// sift
					contents[62] = StringUtils.defaultIfEmpty(item.getSift(), "");
					// mutationtaster
					contents[63] = StringUtils.defaultIfEmpty(item.getMutationtaster(), "");
					// left sequence
					contents[64] = StringUtils.defaultIfEmpty(item.getLeftSeq(), "");
					// right sequence
					contents[65] = StringUtils.defaultIfEmpty(item.getRightSeq(), "");
					spreadSheetList.add(contents);
				}
			}
			completeCount++;
			this.updateProgress(completeCount, searchedSamples.size());
			this.updateMessage("Variant Data Download : " + completeCount + "/" + searchedSamples.size());			
		}
		return spreadSheetList;
	}
	

}
