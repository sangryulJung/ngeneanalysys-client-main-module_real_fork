package ngeneanalysys.model.render;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;

/**
 * Datepicker pattern format init
 * 
 * @author gjyoo
 * @since 2016. 5. 16. 오전 9:58:41
 */
public class DatepickerConverter {

	@SuppressWarnings("rawtypes")
	public static StringConverter getConverter(String pattern) {
		return new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		};
	}
}
