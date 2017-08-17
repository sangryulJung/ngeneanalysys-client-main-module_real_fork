package ngeneanalysys.exceptions;


import javafx.scene.control.Alert;

/**
 * @author Jang
 * @since 2017-08-04
 */
public class DialogException extends Exception {

    private static final long serialVersionUID = -1765624430192908362L;
    private Alert.AlertType alertType;
    private String headerText;
    private String contents;
    private boolean isModal;

    public DialogException(Alert.AlertType alertType, String headerText, String contents, boolean isModal) {
        super(String.format("Header = %s, Contents = %s", headerText, contents));
        this.alertType = alertType;
        this.headerText = headerText;
        this.contents = contents;
        this.isModal = isModal;
    }

    public Alert.AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(Alert.AlertType alertType) {
        this.alertType = alertType;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public boolean isModal() {
        return isModal;
    }

    public void setModal(boolean modal) {
        isModal = modal;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
