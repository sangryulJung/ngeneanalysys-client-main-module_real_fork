package ngeneanalysys.model.render;

/**
 * Combo box Item 키/값 객체
 * 
 * @author gjyoo
 * @since 2016. 5. 23. 오전 10:14:39
 */
public class PanelComboBoxItem extends ComboBoxItem {

	private String code;

	public PanelComboBoxItem(String value, String text, String code) {
		super(value, text);
		this.code = code;
	}

	public PanelComboBoxItem() {
		super();
		this.code = "";
	}

	/**
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}
}
