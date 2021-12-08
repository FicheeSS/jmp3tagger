package ui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.Objects;

public class MainJFX extends Application {
    public static Stage Stage;
    public static final Image APP_ICON = new Image(Objects.requireNonNull(ui.Main.class.getResourceAsStream("LogoAppli.jpg")));
    public static final URL APP_ICONLOC =  Main.class.getResource("LogoAppli.jpg");
    public static FXTrayIcon trayIcon;


    public static void stopTray(){
        trayIcon.hide();
        Platform.exit();
    }
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
        Scene primaryScene = new Scene(root, (screenBounds.getWidth() > 1284) ? 1284 : screenBounds.getWidth(), (screenBounds.getHeight() > 811) ? 811 : screenBounds.getHeight() - 10);
        primaryStage.setScene(primaryScene);
        primaryStage.getIcons().add(APP_ICON);
        primaryStage.setMinHeight((screenBounds.getHeight() > 811) ? 811 : screenBounds.getHeight() - 10);
        primaryStage.setMinWidth((screenBounds.getWidth() > 1284) ? 1284 : screenBounds.getWidth());
        primaryStage.show();
        trayIcon = new FXTrayIcon(MainJFX.Stage,MainJFX.APP_ICONLOC);
        trayIcon.show();
        Stage.setOnCloseRequest((WindowEvent t) -> {
            stopTray();
        });
        MenuItem menuItemTest = new MenuItem("Exit");
        menuItemTest.setOnAction(e -> stopTray()
               );
        trayIcon.addMenuItem(menuItemTest);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ATTENTION");
        alert.setHeaderText("Attention");
        alert.setContentText("Ce programme est très instable et codé par quelqu'un de très instable aussi. A utiliser avec prudence");
        var stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(APP_ICON);
        alert.showAndWait();

    }


}
