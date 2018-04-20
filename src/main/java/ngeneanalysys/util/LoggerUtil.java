package ngeneanalysys.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for common Log4J interactions
 * 
 * {@link "http://spiffyframework.svn.sourceforge.net/viewvc/spiffyframework/tags/v0.05/src/spiffy/log4j/LoggerHelper.java?view=markup"}
 * {@link "http://firstclassthoughts.co.uk/java/getting_log4j_loggers.html"}
 * {@link "http://www.mularien.com/blog/2009/06/01/5-common-log4j-mistakes/"}
 */
public class LoggerUtil {
	
	private LoggerUtil() { throw new IllegalAccessError("LoggerUtil class"); }
	
	/**
	 * Returns a Log4J logger configured as the calling class. This ensures
	 * copy-paste safe code to get a logger instance, an ensures the logger is
	 * always fetched in a consistent manner. 
	 * usage:
	 * 
	 * 
	 * private final static Logger log = LoggerHelper.getLogger();
	 * 
	 * 
	 * Since the logger is found by accessing the call stack it is important,
	 * that references are static.
	 * 
	 * The code is JDK1.4 compatible
	 * 
	 * @since 0.05
	 * @return log4j logger instance for the calling class
	 * @author Kasper B. Graversen
	 */
	public static Logger getLogger() {
		final Throwable t = new Throwable();
		t.fillInStackTrace();
		return LoggerFactory.getLogger(t.getStackTrace()[1].getClassName());
	}
}
