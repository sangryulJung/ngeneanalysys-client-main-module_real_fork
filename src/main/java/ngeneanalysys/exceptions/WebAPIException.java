package ngeneanalysys.exceptions;

import javafx.scene.control.Alert;
import ngeneanalysys.util.httpclient.HttpClientResponse;

/**
 * @author Jang
 * @since 2017-08-04
 */
public class WebAPIException extends DialogException {

    private static final long serialVersionUID = -5919613427439274633L;
    private HttpClientResponse response;
    public WebAPIException(HttpClientResponse response, Alert.AlertType alertType, String headerText, String contents, boolean isModal) {
        super(alertType, headerText, contents, isModal);
        this.response = response;
    }

    public HttpClientResponse getResponse() {
        return response;
    }

    public void setResponse(HttpClientResponse response) {
        this.response = response;
    }

    @Override
    public String getMessage() {
        return this.getContents();
    }
}
