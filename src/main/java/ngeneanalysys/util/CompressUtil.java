package ngeneanalysys.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 파일 압축 관련 유틸
 * 
 * @author gjyoo
 * @since 2016. 8. 29. 오후 1:10:47
 */
public class CompressUtil {
	
	private CompressUtil() { throw new IllegalAccessError("CompressUtil class"); }
	
	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;
	
	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified
	 * by destDirectory (will be created if does not exists)
	 * 
	 * @param zipFile
	 * @param directory
	 * @throws IOException
	 */
	public static void uncompressZip(File zipFile, File directory) throws IOException {
		if (!directory.exists()) {
			directory.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = directory.getAbsolutePath() + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	/**
	 * Extracts a zip entry (file entry)
	 * 
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	/**
	 * tar.gz 압축 해제
	 * @param tarFile : 압축파일
	 * @param dest : 압축해제 위치
	 * @throws IOException
	 */
	public static void uncompressTarGZ(File tarFile, File dest) throws IOException {
		dest.mkdir();
		TarArchiveInputStream tarIn = null;
		tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(tarFile))));

		TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
		// tarIn is a TarArchiveInputStream
		while (tarEntry != null) { // create a file with the same name as the tarEntry
			File destPath = new File(dest, tarEntry.getName());

			if (tarEntry.isDirectory()) {
				destPath.mkdirs();
			} else {
				destPath.createNewFile();

				byte[] btoRead = new byte[1024];

				BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
				int len = 0;

				while ((len = tarIn.read(btoRead)) != -1) {
					bout.write(btoRead, 0, len);
				}

				bout.close();
				btoRead = null;
			}
			tarEntry = tarIn.getNextTarEntry();
		}
		tarIn.close();
	}
}