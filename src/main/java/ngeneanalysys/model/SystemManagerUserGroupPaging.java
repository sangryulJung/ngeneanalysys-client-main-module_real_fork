package ngeneanalysys.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemManagerUserGroupPaging extends CommonPagination implements Serializable {
	private static final long serialVersionUID = 2042354626981108369L;
	@JsonProperty("result")
	public List<UserGroup> list;

	public List<UserGroup> getList() {
		return list;
	}

	public void setList(List<UserGroup> list) {
		this.list = list;
	}
	
}
