package ngeneanalysys.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemManagerUserInfoPaging extends CommonPagination implements Serializable{
	private static final long serialVersionUID = -8797821443466927001L;
	
	@JsonProperty("result")
	private List<User> list;

	public List<User> getList() {
		return list;
	}

	public void setList(List<User> list) {
		this.list = list;
	}
}
