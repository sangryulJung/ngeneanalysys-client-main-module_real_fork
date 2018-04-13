package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.*;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class SystemMenuSupportController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        // Create the dialog Stage
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Support");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    public void openOperationManual() {
        openDoc(CommonConstants.MANUAL_DOC_PATH_OPERATION);
    }

    /**
     * 매뉴얼 문서 화면 출력
     * @param docFileName String
     */
    private void openDoc(String docFileName) {

        File tempDir = new File(CommonConstants.TEMP_PATH);
        if(!tempDir.exists()) {
            tempDir.mkdirs();
        }

        File docFile = new File(tempDir, docFileName);
        try (InputStream inputStream = resourceUtil.getResourceAsStream("/manual/" + docFileName);
             OutputStream outStream = new FileOutputStream(docFile)){

            // 읽어들일 버퍼크기를 메모리에 생성
            byte[] buf = new byte[1024];

            int len = 0;
            // 끝까지 읽어들이면서 File 객체에 내용들을 쓴다
            while ((len = inputStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }

            getMainApp().getHostServices().showDocument(docFile.toURI().toURL().toExternalForm());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openReleaseNotes() {
        getMainApp().getHostServices().showDocument(CommonConstants.RELEASE_NOTE_URL);
    }
}
