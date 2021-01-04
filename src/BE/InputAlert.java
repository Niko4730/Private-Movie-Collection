package BE;

/*
 *
 *@author DennisPC-bit
 */

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Makes input alert
 */
public class InputAlert {
    public void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(AlertType.ERROR);
        alert.setTitle(message);
        alert.setHeaderText(message);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Deletes alert
     * @param deleteConfirmationMessage
     * @return
     */
    public boolean deleteAlert(String deleteConfirmationMessage) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(deleteConfirmationMessage);
        alert.setTitle(deleteConfirmationMessage);
        alert.setHeaderText(deleteConfirmationMessage);
        alert.showAndWait();
        if (alert.getResult().getText().equals("OK"))
            return true;
        else
            return false;
    }

    /**
     * Shows message box
     * @param title
     * @param header
     * @param content
     * @param alertType
     * @return
     */
    public static Optional<ButtonType> showMessageBox(String title, String header, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }
}
