package ngeneanalysys.service.fileanalyse.handler;

import javafx.scene.control.Alert;
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.code.enums.PanelKitCode;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.code.enums.SequencerCode;
import ngeneanalysys.exceptions.FastQFileParsingException;
import ngeneanalysys.model.Sample;
import ngeneanalysys.service.fileanalyse.PanelDetection;
import ngeneanalysys.service.fileanalyse.PrimerCoverageInfo;
import ngeneanalysys.util.LoggerUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class IlluminaMiSeqFileHandler implements FileHandler {
    private static Logger logger = LoggerUtil.getLogger();

    /** 업로드 허용 파일명 패턴 */
    private String allowFileNamePattern = "([a-zA-Z0-9-]+)_([a-zA-Z0-9-]+)_(L[0-9]{3})_(R[12])_([0-9]{3}).fastq.gz";

    /**
     * 파일명 패턴 체크
     *
     * @param fileName
     * @return
     */
    public boolean isValidFileName(String fileName) {
        Pattern pattern = Pattern.compile(allowFileNamePattern);
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }

    /**
     * FASTQ 파일 Pair명 추출
     *
     * @param fileName
     * @return
     */
    public String getFASTQFilePairName(String fileName) {
        String pairName = "";
        if (fileName.contains("_")) {
            String[] arr = fileName.split("_");
            int idx = 0;
            for (String name : arr) {
                if (idx < arr.length - 2) {
                    if (idx > 0)
                        pairName += "_";
                    pairName += name;
                }
                idx++;
            }
        }
        return pairName;
    }

    /**
     * Fastq 확장자 필터 반환
     */
    @Override
    public List<String> getExtensionFilterList() {
        // 기본 파일 화면 출력 확장자 종류 설정
        List<String> extensList = new ArrayList<>();
        extensList.add("*.fastq.gz");
        extensList.add("*.fastq");
        return extensList;
    }

    /**
     * 선택 파일 정보 분석 샘플 객체 목록으로 반환
     * @throws FastQFileParsingException
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Sample> getSampleList(SequencerCode sequencer, List<File> fileList) throws FastQFileParsingException {
        if (fileList == null || fileList.size() == 0 ) {
            throw new FastQFileParsingException(Alert.AlertType.WARNING, "No File Choose",
                    "Try again files choosing", false);
        }

        // 사용자가 직접 선택한 파일목록 Map
        Map<String, Map<String, Object>> chooseFilePairMap = new HashMap<>();
        List<Sample> sampleList = new ArrayList<>();
        for (File file : fileList) {
            String fileName = FilenameUtils.getName(file.getAbsolutePath());
            String pairName = getFASTQFilePairName(fileName);
            boolean isValidName = isValidFileName(fileName);
            logger.info(String.format("absolute path : %s, file name : %s, pair name : %s, file name valid : %s",
                    file.getAbsolutePath(), fileName, pairName, isValidName));
            // 선택된 파일이 파일명 패턴에 일치하는 경우에만 진행함.
            if (fileName.length() > 200) {
                throw new FastQFileParsingException(Alert.AlertType.WARNING,
                        "[File name is Invalid] A FASTQ file with invalid name has been selected.",
                        "file name length limit is 200.\nInvalid file name is " + fileName, false);
            } else if (isValidName) {
                Map<String, Object> pairMap = null;
                List<File> tempFileList = null;
                if (chooseFilePairMap.containsKey(pairName)) {
                    pairMap = chooseFilePairMap.get(pairName);
                } else {
                    pairMap = new HashMap<>();
                }
                if (pairMap.containsKey("fileList")) {
                    tempFileList = ((List<File>)(pairMap.get("fileList")));
                } else {
                    tempFileList = new ArrayList<>();
                }
                tempFileList.add(file);
                pairMap.put("fileList", tempFileList);
                pairMap.put("pairCount", tempFileList.size());
                chooseFilePairMap.put(pairName, pairMap);
            } else {
                StringBuffer msgSb = new StringBuffer();
                msgSb.append(
                        "Only FASTQ files of 'gzipped fastq paired end' format can be selected and uploaded.");
                msgSb.append("\n\nPattern : " + allowFileNamePattern);
                msgSb.append("\n\nex) KSE-1_S1_L001_R1_001.fastq.gz");
                msgSb.append("\nGroup 1: KSE-1 (Sample Name)");
                msgSb.append("\nGroup 2: S1 (Multiplex Identification, MID)");
                msgSb.append("\nGroup 3: L001 (Lane) must 4 characters, started with 'L', followed by 3 numeric characters");
                msgSb.append("\nGroup 4: R1 (Read Number) must 'R1' or 'R2'");
                msgSb.append("\nGroup 5: 001 (Set Number) must numeric 3 characters");
                msgSb.append("\na-z, A-Z, 0-9, -, _ character only");
                msgSb.append("\nInvalid file name is " + fileName);
                throw new FastQFileParsingException(Alert.AlertType.WARNING,
                        "[File name is Invalid] A FASTQ file with invalid name has been selected.",
                        msgSb.toString(), false);
            }
        }

        // 선택한 파일들이 쌍으로 존재하는지 체크
        for (String pairName : chooseFilePairMap.keySet()) {
            Integer pairCount = (Integer) chooseFilePairMap.get(pairName).get("pairCount");
            if (pairCount < 2) {
                throw new FastQFileParsingException(Alert.AlertType.WARNING, "Selected FASTQ File is Not Pair",
                        "Unpaired FASTQ files are selected.\nPair name is " + pairName, false);
            }
        }
        if (chooseFilePairMap.size() > 0) {
            int idx = 0;

            // fastq 매칭 패널 키트 분석 대상 primer code list
            List<String> acceptPrimers = PipelineCode.getPipelineNameListFromSequencer(sequencer);

            for (String pairName : chooseFilePairMap.keySet()) {
                Sample sample = new Sample();
                Map<String, Object> map = chooseFilePairMap.get(pairName);

                /*sample.setPatientId(
                        pairName.substring(0, pairName.substring(0, pairName.lastIndexOf("_")).lastIndexOf("_")));
                sample.setMid(pairName.substring(sample.getPatientId().length() + 1, pairName.lastIndexOf("_")));
                sample.setName(sample.getPatientId() + "_" + sample.getMid() + "_L001");
                sample.setIndex(idx);*/

                // 파일 목록 정보가 존재하는 경우 해당 파일의 Panel Kit 정보 분석 후 정보 삽입
                if (map.containsKey("fileList")) {
                    List<File> files = (List<File>) map.get("fileList");
                    // Map<String, SortedSet<Analyse.PrimerCoverage>>
                    // primerCoverageMap = null;
                    PanelDetection panelDetection = new PanelDetection();
                    PrimerCoverageInfo primerCoverageInfo = null;
                    try {
                        // primerCoverageMap = new
                        // Analyse().getPrimerFreqs(files, maxReads,
                        // acceptPrimers);
                        primerCoverageInfo = panelDetection.getFirstPrimerCoverage(files, acceptPrimers);
                    } catch (Exception e) {
                        throw new FastQFileParsingException(Alert.AlertType.ERROR, "Check Primer Error",
                                "FASTQ File Primer Check Fail!!\n[pair name : " + pairName + "]\n" + e.getMessage(), false);
                    }
                    if (primerCoverageInfo != null && primerCoverageInfo.getPanelCode() != null) {
                        String[] panelCodes = primerCoverageInfo.getPanelCode().split("_");
                        String expected = panelCodes[0] + "_" + panelCodes[1];
                        PanelKitCode panelKit = PanelKitCode.valueOf(expected);
                        ExperimentTypeCode experimentType = PipelineCode
                                .getExperimentTypeFromSequencerAndPanelKit(panelKit, sequencer);

                        //sample.setExperimentType(experimentType.name());
                        //sample.setKit(panelKit.name());
                        // sample.setPrimerCoverage(primerCoverage);
                    } else {
                        //sample.setExperimentType(ExperimentTypeCode.GERMLINE.name());
                        //sample.setKit(PanelKitCode.BRCA_445.name());
                    }

                    // fastq file pair 용량 집계
                    long totalFileSize = 0;
                    for (File file : files) {
                        totalFileSize += file.length();
                    }
                }

                sampleList.add(sample);
                idx++;
            }
        }
        return sampleList;
    }
}
