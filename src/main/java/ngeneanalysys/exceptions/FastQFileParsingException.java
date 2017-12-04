package ngeneanalysys.exceptions;

import javafx.scene.control.Alert.AlertType;

public class FastQFileParsingException extends DialogException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2848789774609458653L;

	public FastQFileParsingException(AlertType alertType, String headerText, String contents, boolean isModal) {
		super(alertType, headerText, contents, isModal);
	}
}
