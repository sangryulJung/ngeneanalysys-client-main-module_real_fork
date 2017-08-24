package ngeneanalysys.util;

/**
 * @author Jang
 * @since 2017-08-23
 */
public class FileUtil {
    private FileUtil() {}

    /**
     * FASTQ 파일 Pair명 추출
     *
     * @param fileName
     * @return
     */
    public static String getFASTQFilePairName(String fileName) {
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
}
