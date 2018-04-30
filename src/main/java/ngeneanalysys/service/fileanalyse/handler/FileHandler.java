package ngeneanalysys.service.fileanalyse.handler;

import ngeneanalysys.code.enums.SequencerCode;
import ngeneanalysys.exceptions.FastQFileParsingException;
import ngeneanalysys.model.Sample;

import java.io.File;
import java.util.List;

/**
 * @author Jang
 * @since 2017-12-04
 */
public interface FileHandler {

    /**
     * Fastq 확장자 필터 반환
     * @return
     */
    List<String> getExtensionFilterList();

    /**
     * 선택 파일 정보 분석 샘플 객체 목록으로 반환
     * @param sequencer
     * @param fileList
     * @return
     * @throws FastQFileParsingException
     */
    List<Sample> getSampleList(SequencerCode sequencer, List<File> fileList) throws FastQFileParsingException;

    String getFASTQFilePairName(String fileName);
}