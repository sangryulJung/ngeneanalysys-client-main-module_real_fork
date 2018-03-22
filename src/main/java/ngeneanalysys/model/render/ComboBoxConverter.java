package ngeneanalysys.model.render;

import java.util.HashMap;
import java.util.Map;

import javafx.util.StringConverter;

/**
 * ComboBox key/value Pair Converter Class
 * 
 * @author gjyoo
 * @since 2016. 5. 23. 오전 10:26:40
 */
public class ComboBoxConverter extends StringConverter<ComboBoxItem> {
	
	private Map<String,ComboBoxItem> mapItems = new HashMap<>();

	/**
	 * 콤보 아이템 텍스트 반환
	 */
	@Override
	public String toString(ComboBoxItem object) {
		mapItems.put(object.getValue(), object);
		return object.getText();
	}

	/**
	 * 지정 키값의 콤보 아이템 객체 반환
	 */
	@Override
	public ComboBoxItem fromString(String string) {
		return mapItems.get(string);
	}

}
