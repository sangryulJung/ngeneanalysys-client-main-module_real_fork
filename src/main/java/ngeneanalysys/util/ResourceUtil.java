package ngeneanalysys.util;

import java.io.InputStream;
import java.net.URL;

import javafx.scene.image.Image;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.model.Panel;

public class ResourceUtil {
	/**
	 * 지정 URL의 InputStream 객체 반환
	 * @param path String
	 * @return InputStream
	 */
	public InputStream getResourceAsStream(String path) {
		return this.getClass().getResourceAsStream(path);
	}

	/**
	 * 지정 URL의 시스템 경로 객체로 반환
	 * @param path String
	 * @return URL
	 */
	public URL getResourceURL(String path) {
		return getClass().getResource(path);
	}
	
	/**
	 * 지정 경로의 이미지 객체 반환
	 * @param path String
	 * @return Image
	 */
	public Image getImage(String path) {
		if(getResourceURL(path) == null) return null;
		return new Image(getResourceAsStream(path));
	}

	public Image getImage(String path, int x, int y) {
		if(getResourceURL(path) == null) return null;
		return new Image(getResourceAsStream(path), x , y ,true, true);
	}

	public String getDefaultColumnOrderResourcePath(Panel panel) {
		String path = null;
		if(PipelineCode.isHemePipeline(panel.getCode())) {
			path = CommonConstants.BASE_HEME_COLUMN_ORDER_PATH;
		} else if(PipelineCode.isSolidPipeline(panel.getCode())) {
			path = CommonConstants.BASE_SOLID_COLUMN_ORDER_PATH;
		} else if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
			path = CommonConstants.BASE_TSTDNA_COLUMN_ORDER_PATH;
		} else if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_CNV_DNA_V2_SNU.getCode())) {
			path = CommonConstants.BASE_BRCA_SNU_COLUMN_ORDER_PATH;
		} else if(PipelineCode.isBRCAPipeline(panel.getCode())) {
			path = CommonConstants.BASE_BRCA_COLUMN_ORDER_PATH;
		} else if(PipelineCode.isHeredPipeline(panel.getCode())) {
			path = CommonConstants.BASE_HERED_COLUMN_ORDER_PATH;
		}
		return path;
	}
}
