package ngeneanalysys.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;

/**
 * MD5 Checksum 생성
 * 
 * @author gjyoo
 * @since 2016. 5. 14. 오후 11:24:03
 */
public class MD5ChecksumUtil {
	private static Logger logger = LoggerUtil.getLogger();

	/**
	 * 체크섬 문자열 생성 [apache DigestUtils 사용]
	 * @param file File
	 * @return String
	 */
	public static String getFileChecksum(File file) {
		String checksum = null;
		try {
			if(file.exists()) {
				checksum = DigestUtils.md5Hex(new FileInputStream(file));
			} else {
				logger.error("target file not exists..");
			}
		} catch (IOException e) {
			logger.error("generate MD5 Checksum Fail : " + e.getMessage());
		}
		return checksum;
	}
}
