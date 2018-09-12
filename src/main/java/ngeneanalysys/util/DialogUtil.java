package ngeneanalysys.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import ngeneanalysys.code.constants.CommonConstants;

/**
 * Dialog 출력 Util Class
 * 
 * @author gjyoo
 * @since 2016. 4. 28. 오후 5:44:52
 */
public class DialogUtil {
	
	private DialogUtil() { throw new IllegalAccessError("DialogUtil class"); }
	
	/**
	 * 일반 Dialog 출력
	 * @param alterType AlertType
	 * @param headerText String
	 * @param contentText String
	 * @param ownerStage Stage
	 * @param waitAfterShow boolean
	 * @return Alert
	 */
	public static Alert generalShow(AlertType alterType, String headerText, String contentText, Stage ownerStage, boolean waitAfterShow) {
 		Alert alert = new Alert(alterType);
		if(ownerStage != null) {
			alert.initOwner(ownerStage);
		}
		
		String title;

		if(alterType == AlertType.WARNING) {
			title = "WARNING";
		} else if(alterType == AlertType.ERROR) {
			title = "ERROR";
		} else {
			title = "INFORMATION";
		}
		setIcon(alert);

		alert.setResizable(true);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		if(StringUtils.isEmpty(contentText)) {
			alert.setContentText("Unknown message.");
		} else {
			alert.setContentText(contentText);
		}
		
		if(waitAfterShow) {
			alert.showAndWait();
		} else {
			alert.show();
		}
		
		return alert;
	}

	/**
	 * INFORMATION 알림창 출력
	 * @param headerText String
	 * @param contentText String
	 * @param ownerStage Stage
	 * @param waitAfterShow boolean
	 * @return Alert
	 */
	public static Alert alert(String headerText, String contentText, Stage ownerStage, boolean waitAfterShow) {
		return generalShow(AlertType.INFORMATION, headerText, contentText, ownerStage, waitAfterShow);
	}
	
	/**
	 * 경고 알림창 출력
	 * @param headerText String
	 * @param contentText String
	 * @param ownerStage Stage
	 * @param waitAfterShow boolean
	 * @return Alert
	 */
	public static Alert warning(String headerText, String contentText, Stage ownerStage, boolean waitAfterShow) {
		return generalShow(AlertType.WARNING, headerText, contentText, ownerStage, waitAfterShow);
	}
	
	/**
	 * 오류 알림창 출력
	 * @param headerText String
	 * @param contentText String
	 * @param ownerStage Stage
	 * @param waitAfterShow boolean
	 * @return Alert
	 */
	public static Alert error(String headerText, String contentText, Stage ownerStage, boolean waitAfterShow) {
		return generalShow(AlertType.ERROR, headerText, contentText, ownerStage, waitAfterShow);
	}

	public static void setIcon(Alert alert) {
		if (System.getProperty("os.name").toLowerCase().contains("window")) {
			ResourceUtil resourceUtil = new ResourceUtil();
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
		}
	}

    public static void setIcon(TextInputDialog alert) {
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            ResourceUtil resourceUtil = new ResourceUtil();
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
    }
}
