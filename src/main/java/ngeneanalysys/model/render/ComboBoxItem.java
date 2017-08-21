package ngeneanalysys.model.render;

/**
 * Combo box Item 키/값 객체
 * 
 * @author gjyoo
 * @since 2016. 5. 23. 오전 10:14:39
 */
public class ComboBoxItem {
	
	/** Combo Item value */
	private String value;
	/** Combo Item display text */
	private String text;
	
	public ComboBoxItem(String value, String text) {
		this.value = value;
		this.text = text;
	}
	
	public ComboBoxItem() {
		this.value = "";
		this.text = "- N/A -";
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}
