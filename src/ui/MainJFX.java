package ui;

import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.*;

import java.util.Objects;

public class MainJFX extends Application {
    private static Scene primaryScene;
    public static Stage Stage ;
    private final Image APP_ICON = new Image(Objects.requireNonNull(ui.Main.class.getResourceAsStream("LogoAppli.jpg")));

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/fxml/mainWindow.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Stage = primaryStage;
        primaryStage.setTitle("JMP3Tagger");
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        primaryScene = new Scene(root, (screenBounds.getWidth() > 1284) ? 1284 : screenBounds.getWidth(), (screenBounds.getHeight() > 811) ? 811 : screenBounds.getHeight() - 10);
        primaryStage.setScene(primaryScene);
        primaryStage.getIcons().add(APP_ICON);
        primaryStage.show();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ATTENTION");
        alert.setHeaderText("Attention");
        alert.setContentText("Ce programme est très instable et codé par quelqu'un de très instable aussi. A utiliser avec prudence");
        alert.showAndWait();

    }
}
