package ui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        Stage.setOnCloseRequest((WindowEvent t) -> stopTray());
        MenuItem menuItemTest = new MenuItem("Exit");
        menuItemTest.setOnAction(e -> stopTray()
               );
        trayIcon.addMenuItem(menuItemTest);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Disclaimer");

        var stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(APP_ICON);
        VBox dialogPaneContent = new VBox();

        Label label = new Label("Disclamer");
        
        String data = new String(
                Files.readAllBytes(
                        Paths.get(Objects.requireNonNull(
                                getClass().getResource("/ui/DISCLAIMER.TXT")).toURI()
                        )
                )
        );

        TextArea textArea = new TextArea();
        textArea.setText(data);
        dialogPaneContent.getChildren().addAll(label, textArea);
        // Set content for Dialog Pane
        alert.getDialogPane().setContent(dialogPaneContent);
        alert.showAndWait();

    }


}
