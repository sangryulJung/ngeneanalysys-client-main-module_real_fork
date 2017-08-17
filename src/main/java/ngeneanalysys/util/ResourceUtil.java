package ngeneanalysys.util;

import java.io.InputStream;
import java.net.URL;

import javafx.scene.image.Image;

public class ResourceUtil {
	/**
	 * 지정 URL의 InputStream 객체 반환
	 * @param path
	 * @return
	 */
	public InputStream getResourceAsStream(String path) {
		return this.getClass().getResourceAsStream(path);
	}

	/**
	 * 지정 URL의 시스템 경로 객체로 반환
	 * @param path
	 * @return
	 */
	public URL getResourceURL(String path) {
		return getClass().getResource(path);
	}
	
	/**
	 * 지정 경로의 이미지 객체 반환
	 * @param path
	 * @return
	 */
	public Image getImage(String path) {
		if(getResourceURL(path) == null) return null;
		return new Image(getResourceAsStream(path));
	}
	

}
