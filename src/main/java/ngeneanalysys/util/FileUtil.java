package ngeneanalysys.util;

import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.model.ReportTemplate;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

    public static String saveVMFile(ReportTemplate reportTemplate) {
        String folderPath = CommonConstants.BASE_FULL_PATH  + File.separator + "fop" + File.separator + reportTemplate.getId();
        String path = folderPath + File.separator + reportTemplate.getName() + ".vm";

        File folder = new File(folderPath);
        try {
            if (!folder.exists()) {
                folder.mkdirs();
            } else {
                FileUtils.cleanDirectory(folder);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

        File file = new File(path);

        try(BufferedWriter fw = new BufferedWriter(new FileWriter(file, false))) {

            fw.write(reportTemplate.getContents());
            fw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return file.getParentFile().getAbsolutePath();
    }
}
