package ngeneanalysys.util;

import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-23
 */
public class FileUtil {
    private static Logger logger = LoggerUtil.getLogger();
    private FileUtil() {}

    /**
     * FASTQ 파일 Pair명 추출
     *
     * @param fileName String
     * @return String
     */
    public static String getFASTQFilePairName(String fileName) {
        StringBuilder pairName = new StringBuilder();
        if (fileName.contains("_")) {
            String[] arr = fileName.split("_");
            int idx = 0;
            for (String name : arr) {
                if (idx < arr.length - 4) {
                    if (idx > 0)
                        pairName.append("_");
                    pairName.append(name);
                }
                idx++;
            }
        }
        return pairName.toString();
    }

    public static Boolean isValidPairedFastqFiles(List<String> fileNames) {
        if(fileNames.size() < 2 || fileNames.size() % 2 != 0){
            return false;
        }
        List<String> sortedFileNames = fileNames.stream().sorted().collect(Collectors.toList());
        for(int i = 0; i< sortedFileNames.size(); i += 2) {
            int indexR1 = sortedFileNames.get(i).lastIndexOf("R1");
            int indexR2 = sortedFileNames.get(i + 1).lastIndexOf("R2");
            if(indexR1 == -1 || indexR2 == -1) {
                return false;
            }
            String r1SampleName = getFASTQFilePairName(sortedFileNames.get(i));
            String r2SampleName = getFASTQFilePairName(sortedFileNames.get(i + 1));
            if(!r1SampleName.equals(r2SampleName)) {
                return false;
            }
        }
        return true;
    }
}
