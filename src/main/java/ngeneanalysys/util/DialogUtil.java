package ngeneanalysys.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

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
	 * @param alterType
	 * @param headerText
	 * @param contentText
	 * @param ownerStage
	 * @param waitAfterShow
	 * @return
	 */
	public static Alert generalShow(AlertType alterType, String headerText, String contentText, Stage ownerStage, boolean waitAfterShow) {
		Alert alert = new Alert(alterType);
		if(ownerStage != null) {
			alert.initOwner(ownerStage);
		}
		
		String title = null;
		
	/*	if(alterType == AlertType.INFORMATION) {
			title = "INFORMATION";
		} else */
		if(alterType == AlertType.WARNING) {
			title = "WARNING";
		} else if(alterType == AlertType.ERROR) {
			title = "ERROR";
		} else {
			title = "INFORMATION";
		}

		alert.setResizable(true);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		if(StringUtils.isEmpty(contentText)) {
			alert.setContentText("Unknown message.");
		} else {
			/*if(contentText.length() > 100) {
				alert.setContentText(contentText.substring(0, 100));
			} else {
				alert.setContentText(contentText);
			}*/
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
	 * @param headerText
	 * @param contentText
	 * @param ownerStage
	 * @param waitAfterShow
	 * @return
	 */
	public static Alert alert(String headerText, String contentText, Stage ownerStage, boolean waitAfterShow) {
		return generalShow(AlertType.INFORMATION, headerText, contentText, ownerStage, waitAfterShow);
	}
	
	/**
	 * 경고 알림창 출력
	 * @param headerText
	 * @param contentText
	 * @param ownerStage
	 * @param waitAfterShow
	 * @return
	 */
	public static Alert warning(String headerText, String contentText, Stage ownerStage, boolean waitAfterShow) {
		return generalShow(AlertType.WARNING, headerText, contentText, ownerStage, waitAfterShow);
	}
	
	/**
	 * 오류 알림창 출력
	 * @param headerText
	 * @param contentText
	 * @param ownerStage
	 * @param waitAfterShow
	 * @return
	 */
	public static Alert error(String headerText, String contentText, Stage ownerStage, boolean waitAfterShow) {
		return generalShow(AlertType.ERROR, headerText, contentText, ownerStage, waitAfterShow);
	}
}
