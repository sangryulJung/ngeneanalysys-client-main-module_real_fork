package ngeneanalysys.util;

import java.net.URL;

import javafx.fxml.FXMLLoader;

/**
 * JavaFX FXML Util Class
 * 
 * @author gjyoo
 * @since 2016. 5. 3. 오전 10:53:43
 */
public class FXMLLoadUtil {
	
	private FXMLLoadUtil() { throw new IllegalAccessError("FXMLLoadUtil class"); }

	/**
	 * 지정 경로의 FXML 로드
	 * @param fxmlPath
	 * @return
	 */
	public static FXMLLoader load(String fxmlPath) {
		ResourceUtil resourceUtil = new ResourceUtil();
		return load(resourceUtil.getResourceURL(fxmlPath));
	}
	
	/**
	 * 지정 경로의 FXML 로드
	 * @param url
	 * @return
	 */
	public static FXMLLoader load(URL url) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(url);
		return loader;
	}
}
