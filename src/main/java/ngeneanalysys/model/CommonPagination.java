package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 페이지 기본 정보
 * 
 * @author gjyoo
 * @since 2016. 6. 1. 오후 2:50:16
 */
public class CommonPagination {
	
	/** 목록수 */
	@JsonProperty("count")
	private Integer count;
	/** 다음페이지 URL */
	@JsonProperty("next")
	private String next;
	/** 이전페이지 URL */
	@JsonProperty("previous")
	private String previous;
	
	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}
	/**
	 * @return the next
	 */
	public String getNext() {
		return next;
	}
	/**
	 * @param next the next to set
	 */
	public void setNext(String next) {
		this.next = next;
	}
	/**
	 * @return the previous
	 */
	public String getPrevious() {
		return previous;
	}
	/**
	 * @param previous the previous to set
	 */
	public void setPrevious(String previous) {
		this.previous = previous;
	}
}
