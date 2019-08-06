package ngeneanalysys.model.render;

import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * ComboBox key/value Pair Converter Class
 * 
 * @author gjyoo
 * @since 2016. 5. 23. 오전 10:26:40
 */
public class PanelComboBoxConverter extends StringConverter<PanelComboBoxItem> {
	
	private Map<String,PanelComboBoxItem> mapItems = new HashMap<>();

	/**
	 * 콤보 아이템 텍스트 반환
	 */
	@Override
	public String toString(PanelComboBoxItem object) {
		if(object == null) return "";
		mapItems.put(object.getValue(), object);
		return object.getText();
	}

	/**
	 * 지정 키값의 콤보 아이템 객체 반환
	 */
	@Override
	public PanelComboBoxItem fromString(String string) {
		return mapItems.get(string);
	}

}
