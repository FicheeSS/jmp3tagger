package ui.fxml;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.MainJFX;

import java.io.PrintWriter;
import java.io.StringWriter;

import static ui.MainJFX.trayIcon;

public abstract class BaseControlleur implements Initializable {
    public void showTrayMessage(String s){
        trayIcon.showInfoMessage("JMP3Tagger", s);
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Create and show an exception popup
     * @param e Exeption
     */
    protected void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText(e.getMessage());

        VBox dialogPaneContent = new VBox();

        Label label = new Label("Stack Trace:");

        String stackTrace = this.getStackTrace(e);
        TextArea textArea = new TextArea();
        textArea.setText(stackTrace);
        var stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(MainJFX.APP_ICON);
        dialogPaneContent.getChildren().addAll(label, textArea);

        // Set content for Dialog Pane
        alert.getDialogPane().setContent(dialogPaneContent);

        alert.showAndWait();
    }

    protected void showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        var stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(MainJFX.APP_ICON);
        alert.setTitle("Erreur");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
