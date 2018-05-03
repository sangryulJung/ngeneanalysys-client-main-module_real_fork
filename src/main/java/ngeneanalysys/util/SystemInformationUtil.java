package ngeneanalysys.util;

import java.awt.GraphicsEnvironment;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

import org.slf4j.Logger;

import com.sun.management.OperatingSystemMXBean;

/**
 * 시스템 환경 정보 유틸 클래
 * @author gjyoo
 * @since 2016.08.18
 *
 */
@SuppressWarnings("restriction")
public class SystemInformationUtil {
	private static Logger logger = LoggerUtil.getLogger();

	OperatingSystemMXBean osMXBean = null;
	
	public SystemInformationUtil() {
		osMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	}
	
	/**
	 * 시스템 정보 로그 출력
	 */
	public void printSystemInformationLog() {
		logger.debug("### SYSTEM INFORMATION ###");
		logger.debug(String.format("- OS [name : %s, version : %s, architecher : %s]", getOSName(), getOSVersion(), getOSArchitecher()));
		logger.debug(String.format("- MEMORY [physical : %s, swap : %s, graphic : %s]", getTotalPhysicalMemorySizeToConvertFormat(), getTotalSwapSpaceSizeToConvertFormat(), getGraphicMemorySizeToConvertFormat()));
		logger.debug(String.format("- CPU [processor(thread) : %s]", getCPUThreadCount()));
		logger.debug("### SYSTEM INFORMATION ###");
	}
	
	/**
	 * CPU 사용현황 로그 출력
	 */
	public void printSystemCPUUsageLog() {
		logger.debug(String.format("### CPU Usage [load avg : %s, processors : %s, systme usage : %s]", String.format("%.2f(%%)", getCPULoadAvg()), String.format("%.2f(%%)", getCPUProcessLoad()), String.format("%.2f(%%)", getCPUSystemLoad())));
	}

	/**
	 * 파일 용량 단위 형식의 문자열로 반환
	 * @param size
	 * @return
	 */
	public String convertFileSizeFormat(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	
	/**
	 * OS명 반
	 * @return String
	 */
	public String getOSName() {
		return osMXBean.getName();
	}
	
	/**
	 * OS 버전 반
	 * @return String
	 */
	public String getOSVersion() {
		return osMXBean.getVersion();
	}
	
	/**
	 * OS 아키텍쳐 정보 반환 ex) x86_64
	 * @return String
	 */
	public String getOSArchitecher() {
		return osMXBean.getArch();
	}
	
	/**
	 * CPU Thread 수 반환
	 * @return int
	 */
	public int getCPUThreadCount() {
		return osMXBean.getAvailableProcessors();
	}
	
	/**
	 * 물리 메모리 사이즈 반환
	 * @return long
	 */
	public long getTotalPhysicalMemorySize() {
		return osMXBean.getTotalPhysicalMemorySize();
	}
	
	/**
	 * 물리 메모리 사이즈 단위 변환 반환 ex) 1024 -> 1KB
	 * @return String
	 */
	public String getTotalPhysicalMemorySizeToConvertFormat() {
		return convertFileSizeFormat(getTotalPhysicalMemorySize());
	}
	
	/**
	 * Swap 공간 메모리 사이즈 반환
	 * @return long
	 */
	public long getTotalSwapSpaceSize() {
		return osMXBean.getTotalSwapSpaceSize();
	}
	
	/**
	 * Swap 공간 메모리 사이즈 단위 변환 반환 ex) 1024 -> 1KB
	 * @return String
	 */
	public String getTotalSwapSpaceSizeToConvertFormat() {
		return convertFileSizeFormat(getTotalSwapSpaceSize());
	}
	
	/**
	 * 그래픽 메모리 사이즈 반환
	 * @return long
	 */
	public long getGraphicMemorySize() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getAvailableAcceleratedMemory();
	}

	/**
	 * 그래픽 메모리 사이즈 단위 변환 반환 ex) 1024 -> 1KB
	 * @return String
	 */
	public String getGraphicMemorySizeToConvertFormat() {
		return convertFileSizeFormat(getGraphicMemorySize());
	}

	/**
	 * 최근 수 분 동안 시스템의 평균 부하를 반환
	 * @return double
	 */
	public double getCPULoadAvg() {
		return osMXBean.getSystemLoadAverage();
	}
	
	/**
	 * JVM Process의 CPU 사용값을 0.0~1.0으로 반환 
	 * @return double
	 */
	public double getCPUProcessLoad() {
		return osMXBean.getProcessCpuLoad();
	}
	
	/**
	 * 시스템의 CPU 사용값을 0.0~1.0으로 반환
	 * @return double
	 */
	public double getCPUSystemLoad() {
		return osMXBean.getSystemCpuLoad();
	}
}