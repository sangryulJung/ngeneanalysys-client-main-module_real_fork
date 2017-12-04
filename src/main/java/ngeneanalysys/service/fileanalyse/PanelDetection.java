package ngeneanalysys.service.fileanalyse;

import ngeneanalysys.util.LoggerUtil;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.slf4j.Logger;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class PanelDetection {
    final private int maxReadCount = 30000;
    final static Logger logger = LoggerUtil.getLogger();

    public PrimerCoverageInfo getFirstPrimerCoverage(List<File> pairedFiles, List<String> availablePanels)
            throws Exception {
        final PrimerCoverageInfo firstPrimerCoverageInfo = new PrimerCoverageInfo();
        // 패널별 프라이머(primerid, sequence) 맵 정보
        final Map<String, Map<String, String>> primerPerPanelMap = this.getPrimerPerPanelMap(availablePanels);
        // 패널별 프라이머의 개수
        final Map<String, Integer> primerCountPerPanelMap = new HashMap<>();
        // 패널별로 primer별로 hit count
        final Map<String, Map<String, Integer>> matchCountPerPrimerPerPanel = new HashMap<>();
        // primer를 검색할 Trie를 패널 별로 생성한다. 패널 간에 중복된 primer를 분리하기 위해서.
        final Map<String, Trie> triePerPanelMap = new HashMap<String, Trie>();
        // 패널 별로 매치된 카운트 값
        final Map<String, Integer> matchTotalCountPerPanel = new HashMap<>();
        // paired file에 대해서 전체 read 수
        int totalReadCount = 0;

        // final primerPerPanelMap.con
        for (String panelCode : primerPerPanelMap.keySet()) {
            // System.out.println(key);
            Trie.TrieBuilder trieBuiler = Trie.builder();
            int primerCount = 0;
            for (String primerKey : primerPerPanelMap.get(panelCode).keySet()) {
                trieBuiler.addKeyword(primerKey);
                primerCount++;
            }
            triePerPanelMap.put(panelCode, trieBuiler.build());
            primerCountPerPanelMap.put(panelCode, primerCount);
        }
        // fastq 파일을 읽어서 primer를 탐색한다.
        for (File fastqFile : pairedFiles) {
            Map<String, Map<String, Integer>> primerSearchResult = getFastQReadResult(fastqFile, triePerPanelMap);
            totalReadCount += primerSearchResult.get("fastqReadCount").get("fastqReadCount");
            for (String panelCode : primerSearchResult.keySet()) {
                if ("fastqReadCount".equals(panelCode))
                    continue;
                if (!matchTotalCountPerPanel.containsKey(panelCode)) {
                    matchTotalCountPerPanel.put(panelCode, 0);
                }
                if (!matchCountPerPrimerPerPanel.containsKey(panelCode)) {
                    matchCountPerPrimerPerPanel.put(panelCode, new HashMap<String, Integer>());
                }
                int current = 0;
                for (String primerKey : primerSearchResult.get(panelCode).keySet()) {
                    current = matchTotalCountPerPanel.get(panelCode);
                    matchTotalCountPerPanel.put(panelCode, current + primerSearchResult.get(panelCode).get(primerKey));
                    if (!matchCountPerPrimerPerPanel.get(panelCode).containsKey(primerKey)) {
                        matchCountPerPrimerPerPanel.get(panelCode).put(primerKey,
                                primerSearchResult.get(panelCode).get(primerKey));
                    } else {
                        current = matchCountPerPrimerPerPanel.get(panelCode).get(primerKey);
                        matchCountPerPrimerPerPanel.get(panelCode).put(primerKey,
                                current + primerSearchResult.get(panelCode).get(primerKey));
                    }
                }
            }
        }

        firstPrimerCoverageInfo.setTotalReadCount(totalReadCount);
        for (String panelCode : matchTotalCountPerPanel.keySet()) {
            Integer matchCount = matchTotalCountPerPanel.get(panelCode);
            Double matchPercentage = 100.0 * matchCount / totalReadCount;
            Double coverage = 100.0 * matchCountPerPrimerPerPanel.get(panelCode).size()
                    / primerCountPerPanelMap.get(panelCode);
            logger.info(String.format("Panel = %s, match count = %s, matchPercentage = %s, coverage = %s",
                    panelCode, matchCount, matchPercentage, coverage));
            if (matchPercentage < 50.0 || coverage < 50.0) {
                continue;
            }
            if (firstPrimerCoverageInfo.getPanelCode() == null) {
                firstPrimerCoverageInfo.setPanelCode(panelCode);
                firstPrimerCoverageInfo.setCoverage(coverage);
                firstPrimerCoverageInfo.setMatchPercentage(matchPercentage);
                firstPrimerCoverageInfo.setTotalMatchPrimerCount(matchCount);
            } else if ((firstPrimerCoverageInfo.getMatchPercentage() > 99.0
                    && firstPrimerCoverageInfo.getCoverage() > 90.0 && matchPercentage < 99.0)
                    || (firstPrimerCoverageInfo.getMatchPercentage() > 90.0
                    && firstPrimerCoverageInfo.getCoverage() > 99.0 && coverage < 99.0)) {
                continue;
            } else if ((matchPercentage > 99.0 && coverage > 90.0
                    && firstPrimerCoverageInfo.getMatchPercentage() < 99.0)
                    || (matchPercentage > 90.0 && coverage > 99.0 && firstPrimerCoverageInfo.getCoverage() < 99.0)
                    || (firstPrimerCoverageInfo.getMatchPercentage()
                    + firstPrimerCoverageInfo.getCoverage() < matchPercentage + coverage)) {
                firstPrimerCoverageInfo.setPanelCode(panelCode);
                firstPrimerCoverageInfo.setCoverage(coverage);
                firstPrimerCoverageInfo.setMatchPercentage(matchPercentage);
                firstPrimerCoverageInfo.setTotalMatchPrimerCount(matchCount);
            } else {
                continue;
            }
            // System.out.println("key = " + panelCode + ", count = " +
            // matchTotalCountPerPanel.get(panelCode)
            // + ", coverage = " + coverage);
        }
        // System.out.println("totalReadCount = " + totalReadCount);
        return firstPrimerCoverageInfo;
    }

    private Map<String, Map<String, String>> getPrimerPerPanelMap(List<String> availablePanels) throws Exception {
        final Map<String, Map<String, String>> primerPerPanelMap = new HashMap<String, Map<String, String>>();
        final BufferedReader br = new BufferedReader(
                new InputStreamReader(PanelDetection.class.getResourceAsStream("/primers/list.txt")));
        try {
            String panelPrimerLine;
            while ((panelPrimerLine = br.readLine()) != null) {
                final String[] panelPrimerItems = panelPrimerLine.split("\t");
                final String panelId = panelPrimerItems[0];// + "_" + panelPrimerItems[1];
                final String primerInfoFileName = panelPrimerItems[2];
                if (availablePanels.contains(panelPrimerItems[0])) {
                    final Map<String, String> primerMap = new HashMap<String, String>();
                    try {
                        BufferedReader primerDetailBuf = new BufferedReader(new InputStreamReader(
                                PanelDetection.class.getResourceAsStream("/primers/" + primerInfoFileName)));
                        String primerDetailLine = null;
                        while ((primerDetailLine = primerDetailBuf.readLine()) != null) {
                            String[] primerDetailItems = primerDetailLine.split("\t");
                            primerMap.put(primerDetailItems[1], primerDetailItems[0]);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        throw new RuntimeException(
                                "Panel primer data read fail. Primer data file name = " + primerInfoFileName);
                    }
                    primerPerPanelMap.put(panelId, primerMap);
                    // System.out.println("primer file = " +
                    // primerInfoFileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return primerPerPanelMap;
    }

    /*
     * 입력된 fastq 파일을 읽고 패널 별로 Trie 검색을한다. 패널 별로 매칭된 primer 맵을 리턴한다. primer 맵은
     * primer sequence string, match count로 구성된다.
     */
    private Map<String, Map<String, Integer>> getFastQReadResult(final File file,
                                                                 final Map<String, Trie> triePerPanelMap) throws IOException {
        final Map<String, Map<String, Integer>> resultMap = new HashMap<>();
        final BufferedInputStream br;
        if (file.getName().endsWith(".gz") || file.getName().endsWith(".GZ")) {
            br = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)));
        } else {
            br = new BufferedInputStream(new FileInputStream(file));
        }
        br.mark(0);
        BufferedReader reader = new BufferedReader(new InputStreamReader(br));
        int readCount = 0;
        int lineNumber = 0;
        String seqHeader;
        String seqData;
        String seqQualityHeader;
        String seqQualityData;
        try {
            while ((seqHeader = reader.readLine()) != null) {
                lineNumber++;
                // Seq Header
                if (!seqHeader.startsWith("@")) {
                    String errorMsg = "Invalid Sequence Header. File Name = " + file.getName() + ", Line = "
                            + lineNumber;
                    logger.error(errorMsg);
                    throw new IOException(errorMsg);
                }
                // Seq Data
                seqData = reader.readLine();
                lineNumber++;
                if (seqData == null || seqData.isEmpty()) {
                    String errorMsg = "Invalid Sequence Data. File Name = " + file.getName() + ", Line = " + lineNumber;
                    logger.error(errorMsg);
                    throw new IOException(errorMsg);
                }
                // Seq Quality Header
                seqQualityHeader = reader.readLine();
                lineNumber++;
                if (seqQualityHeader == null || seqQualityHeader.isEmpty() || !seqQualityHeader.startsWith("+")) {
                    String errorMsg = "Invalid Sequence Quality Header. File Name = " + file.getName() + ", Line = "
                            + lineNumber;
                    logger.error(errorMsg);
                    throw new IOException(errorMsg);
                }
                // Seq Quality Data
                seqQualityData = reader.readLine();
                lineNumber++;
                if (seqQualityData == null || seqQualityData.isEmpty() || seqData.length() != seqQualityData.length()) {
                    String errorMsg = "Invalid Sequence Quality Data. File Name = " + file.getName() + ", Line = "
                            + lineNumber;
                    logger.error(errorMsg);
                    throw new IOException(errorMsg);
                }
                readCount++;
                for (String panelCode : triePerPanelMap.keySet()) {
                    Collection<Emit> emits = triePerPanelMap.get(panelCode).parseText(seqData);
                    if (!emits.isEmpty()) {
                        if (!resultMap.containsKey(panelCode)) {
                            resultMap.put(panelCode, new HashMap<String, Integer>());
                        }
                        for (Emit emit : emits) {
                            if (!resultMap.get(panelCode).containsKey(emit.getKeyword())) {
                                resultMap.get(panelCode).put(emit.getKeyword(), 1);
                            } else {
                                int currentVal = resultMap.get(panelCode).get(emit.getKeyword());
                                resultMap.get(panelCode).put(emit.getKeyword(), currentVal + 1);
                            }
                        }
                    }
                }
                if (readCount >= maxReadCount) {
                    break;
                }
            }
            Map<String, Integer> tempObj = new HashMap<>();
            tempObj.put("fastqReadCount", readCount);
            resultMap.put("fastqReadCount", tempObj);
            return resultMap;
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static void main(String[] args) {
        PanelDetection panelDetection = new PanelDetection();
        List<String> availablePanels = new ArrayList<>();
        availablePanels.add("BRCA_445_MISEQ");
        availablePanels.add("BRCA_446_MISEQ");
        availablePanels.add("BRCA_998_MISEQ");
        List<File> fileList = new ArrayList<>();
        fileList.add(new File("C:/Users/joonsik/Documents/BRCAaccuTest_new/GM12878_S24_L001_R1_001.fastq.gz"));
        fileList.add(new File("C:/Users/joonsik/Documents/BRCAaccuTest_new/GM12878_S24_L001_R2_001.fastq.gz"));
        try {
            PrimerCoverageInfo primerCoverageInfo = panelDetection.getFirstPrimerCoverage(fileList, availablePanels);
            System.out.println(primerCoverageInfo.getPanelCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * Trie trie =
         * Trie.builder().addKeyword("hers").addKeyword("his").addKeyword("his")
         * .addKeyword("she") .addKeyword("he").build(); Collection<Emit> emits
         * = trie.parseText("ushers"); for (Emit emit : emits) {
         * System.out.println(emit.getKeyword()); }
         */

    }
}
