package ngeneanalysys.util;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import ngeneanalysys.code.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.slf4j.Logger;

/**
 * Velocity Util
 * 
 * @author gjyoo
 * @since 2016. 9. 22. 오전 10:25:18
 */
public class VelocityUtil {
	private static Logger logger = LoggerUtil.getLogger();
	
	private VelocityEngine engine;
	
	/** Init VelocityEngine */
	public void init() {
		logger.debug("Init Velocity Engine");
		engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file,classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
		engine.setProperty("file.resource.loader.path", CommonConstants.BASE_FULL_PATH  + File.separator + "fop");
		engine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");
		engine.init();
	}
	
	/**
	 * 지정 argument 템플릿에 적용 후 내용 반환
	 * @param templatePath String
	 * @param encoding String
	 * @param model Map<String,Object>
	 * @return String
	 */
	public String getContents(String templatePath, String encoding, Map<String,Object> model) {
		logger.debug(String.format("template [%s], encoding [%s]", templatePath, encoding));

		if(engine == null) init();
		if(StringUtils.isEmpty(encoding)) encoding = CommonConstants.ENCODING_TYPE_UTF;

		VelocityContext context = new VelocityContext(model);
		StringWriter sw = new StringWriter();

		// 템플릿 조회
		Template template = engine.getTemplate(templatePath, encoding);
		// context와 템플릿 merge
		template.merge(context, sw);
		return sw.toString();
	}
}
