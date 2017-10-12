package ngeneanalysys.service;

import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.ResourceUtil;
import org.apache.commons.io.FileUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * @author Jang
 * @since 2017-10-11
 */
public class PDFCreateService {
    private static Logger logger = LoggerUtil.getLogger();

    /** FOP Config XML 경로 */
    private File fopConfDir = new File(CommonConstants.BASE_FULL_PATH, "fop");

    /** FOP 설정 파일 */
    private File fopConfXML = new File(fopConfDir, "fop.conf.xml");

    /** FOP Font 파일 경로 */
    private File fontDir = new File(fopConfDir, "font");

    /** FOP 사용 Font 목록 */
    private String[] fontList = new String[]{"NanumSquareR.ttf", "NanumSquareB.ttf","NotoSansCJKkr-Regular.otf", "NotoSansCJKkr-Bold.otf"};

    /**
     * 환경 체크
     * 	- FOP 관련 설정 XML 및 폰트 파일들이 .ngenebio_analysys_gui 경로 하위에 복사되어 있는지 체크
     * @return
     */
    public boolean checkEnvironment() {
        if(!fopConfXML.exists()) return false;
        if(!fontDir.exists()) return false;
        boolean fontExist = true;
        for (String font : fontList) {
            File tempTTF = new File(fontDir, font);
            if(!tempTTF.exists()) {
                fontExist = false;
                break;
            }
        }
        return fontExist;
    }

    /**
     * 설정 파일을 .ngenebio_analysys_gui 경로 하위에 복사
     * @see FOP Library를 사용하여 PDF 생성 시 설정파일과 폰트파일들이 jar파일 내에 존재하는 경우 파일을 정상적으로 불러올수 없어 특정 경로에 복사하여 사용함.
     */
    public void copyFopConfig() throws Exception {
        logger.info("copy fop config files..");
        ResourceUtil resourceUtil = new ResourceUtil();

        // ${user.home}/.ngenebio_analysys_gui/fop/font 디렉토리 존재 체크
        if(!fontDir.exists()) {
            logger.info("create directory : " + fontDir.getAbsolutePath());
            fontDir.mkdirs();
        }

        // 설정 xml 파일 복사
        InputStream fopConfigStream = resourceUtil.getResourceAsStream("/fop.conf.xml");
        File tempConfig = new File(fopConfDir, "fop.conf.xml");
        FileUtils.copyInputStreamToFile(fopConfigStream, tempConfig);
        logger.info("copy config xml..");

        // 폰트 파일 복사
        for (String font : fontList) {
            logger.info(String.format("copy font [%s]", font));

            // ttf 파일 복사
            InputStream ttfStream = resourceUtil.getResourceAsStream("/layout/font/" + font);
            File tempTTF = new File(fontDir, font);
            FileUtils.copyInputStreamToFile(ttfStream, tempTTF);
        }
    }

    /**
     * PDF 파일 생성
     * @param saveFile
     * @param contents
     * @return
     * @throws Exception
     */
    public boolean createPDF(File saveFile, String contents) throws Exception {
        boolean result = true;
        OutputStream out = null;

        try {
            // fop 관련 파일들이 .ngenebio_analysys_gui 경로 하위에 존재하는지 체크하여 존재하지 않는 경우 복사 처리함.
//			if(!checkEnvironment()) {
            copyFopConfig();
//			}

            logger.info(String.format("create pdf [%s]", saveFile.getAbsolutePath()));

            FopFactory fopFactory = FopFactory.newInstance(fopConfXML);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            // Setup output stream.  Note: Using BufferedOutputStream
            // for performance reasons (helpful with FileOutputStreams).
            out = new FileOutputStream(saveFile);
            out = new BufferedOutputStream(out);

            // Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity transformer

            // Setup input stream
            String foContents = contents;
            Source src = new StreamSource(new StringReader(foContents));

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);

            out.flush();
        } catch (RuntimeException re) {
            logger.error(re.getMessage());
            throw re;
        } catch (Throwable e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return result;
    }
}
