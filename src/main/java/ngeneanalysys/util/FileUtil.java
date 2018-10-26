package ngeneanalysys.util;

import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.model.AnalysisFile;
import ngeneanalysys.model.ReportTemplate;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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

    public static String saveVMFile(ReportTemplate reportTemplate) {
        String folderPath = CommonConstants.BASE_FULL_PATH  + File.separator + "fop" + File.separator + reportTemplate.getId();
        String path = folderPath + File.separator + reportTemplate.getName() + ".vm";

        File folder = new File(folderPath);
        try {
            if (!folder.exists()) {
                if(!folder.mkdirs()) {
                    return "";
                }
            } else {
                FileUtils.cleanDirectory(folder);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return "";
        }

        File file = new File(path);

        try(BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"))) {
            fw.write(reportTemplate.getContents());
            fw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return file.getParentFile().getAbsolutePath();
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

    public static String downloadCNVImage(AnalysisFile analysisFile) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        APIService apiService = APIService.getInstance();
        String tempPath = CommonConstants.BASE_FULL_PATH  + File.separator + "temp";
        File tempFile = new File(tempPath);

        if(!tempFile.exists()) {
            tempFile.mkdirs();
        }

        String downloadUrl = "/analysisFiles/" + analysisFile.getSampleId() + "/" + analysisFile.getName();
        String path = CommonConstants.BASE_FULL_PATH  + File.separator + "temp" +
                File.separator + analysisFile.getName();

        File file = new File(path);

        OutputStream os = null;
        try {
            String connectURL = apiService.getConvertConnectURL(downloadUrl);

            // 헤더 삽입 정보 설정
            Map<String,Object> headerMap = apiService.getDefaultHeaders(true);

            HttpGet get = new HttpGet(connectURL);
            logger.debug("GET:" + get.getURI());

            // 지정된 헤더 삽입 정보가 있는 경우 추가
            if(headerMap != null && headerMap.size() > 0) {
                for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
                    get.setHeader(entry.getKey(), entry.getValue().toString());
                }
            }

            httpclient = HttpClients.custom().setSSLSocketFactory(HttpClientUtil.getSSLSocketFactory()).build();
            if (httpclient != null)
                response = httpclient.execute(get);
            if (response == null){
                logger.error("httpclient response is null");
                throw new NullPointerException();
            }
            int status = response.getStatusLine().getStatusCode();

            if(status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                os = Files.newOutputStream(Paths.get(file.toURI()));

                byte[] buf = new byte[8192];
                int n;
                while ((n = content.read(buf)) > 0) {
                    os.write(buf, 0, n);
                }
                content.close();
                os.flush();
                if (httpclient != null) httpclient.close();
                if (response != null) response.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return path;
    }
}
