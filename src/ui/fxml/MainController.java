package ui.fxml;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.javatuples.Pair;
import ui.IDObject;
import ui.Main;
import ui.MainJFX;
import ui.TagsTypes;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static ui.MainJFX.trayIcon;

public class MainController implements Initializable {
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private final ObservableList<MessageObject> items = FXCollections.observableArrayList();
    public TextField folder;
    public ListView<MessageObject> mp3List = new ListView<MessageObject>();
    public Button updFile;
    public Button searchLyrics;
    public TextArea lyricsField;
    public TextField artistName;
    public TextField trackName;
    public TextField year;
    public TextField albumName;
    public TextField regularExpr;
    private MessageObject currentMessage;
    public ProgressBar applyBar;
    public File selectedDirectory;
    private final ArrayList<IDObject> MP3FileList = new ArrayList<IDObject>();

    @FXML
    private CheckBox recursage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mp3List.setItems(items);
        mp3List.setCellFactory(param -> new ListCell<>() {
            @Override
            public void updateItem(MessageObject message, boolean empty) {
                //imageView.setImage();
                super.updateItem(message, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(message.getText());
                }
            }
        });
        if (Main.lyrcisFetcher == null) {
            searchLyrics.setDisable(true);
        }


    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String s = sw.toString();
        return s;
    }

    /**
     * Create and show an exception popup
     * @param e Exeption
     */
    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText(e.getMessage());

        VBox dialogPaneContent = new VBox();

        Label label = new Label("Stack Trace:");

        String stackTrace = this.getStackTrace(e);
        TextArea textArea = new TextArea();
        textArea.setText(stackTrace);

        dialogPaneContent.getChildren().addAll(label, textArea);

        // Set content for Dialog Pane
        alert.getDialogPane().setContent(dialogPaneContent);

        alert.showAndWait();
    }
    protected void showTrayMessage(String s){
        trayIcon.showInfoMessage("JMP3Tagger", s);
    }

    protected void showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Erreur");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public void addFileToDisp(File f) {
        items.add(new MessageObject(f.getName(), f));
    }

    /**
     * Research the directory
     */
    public void refreshDirectory(){
        ArrayList<String> mp3Files = new ArrayList<String>();
        if(recursage.isSelected()) {
            try (Stream<Path> walkStream = Files.walk(selectedDirectory.toPath())) {
                walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                    if (f.toString().endsWith(".mp3")) {
                        mp3Files.add(f.toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            for (var f : Objects.requireNonNull(selectedDirectory.list())) {
                if (f.endsWith(".mp3")) {
                    mp3Files.add(selectedDirectory.getAbsolutePath() + "\\" + f);
                }
            }
        }
        MP3FileList.clear();
        items.clear();
        for (var s : mp3Files) {
            File f = new File(s);
            addFileToDisp(f);
            IDObject o = new IDObject(f);
            MP3FileList.add(o);
        }
    }
    public void onSelectDirectory() {
        selectedDirectory = directoryChooser.showDialog(MainJFX.Stage);
        if (selectedDirectory != null) {
            folder.setText(selectedDirectory.getAbsolutePath());
        } else {
            return;
        }
        refreshDirectory();

    }

    public void onSearchLyrics() {
        if (currentMessage == null) {
            showError("No file selected");
            return;
        }
        try {
            lyricsField.setText(Main.lyrcisFetcher.GetLyricsFromArtistNameAndTrackName(artistName.getText(), trackName.getText()));
        }catch (RuntimeException e ){
            showTrayMessage(e.getMessage());
        }
    }

    public void onUpdate() {
        IDObject idO = new IDObject(currentMessage.getFile());
        ArrayList<Pair<TagsTypes.Tags, String>> tags = new ArrayList<Pair<TagsTypes.Tags, String>>();
        tags.add(Pair.with(TagsTypes.Tags.ARTIST, artistName.getText()));
        tags.add(Pair.with(TagsTypes.Tags.TITLE, trackName.getText()));
        tags.add(Pair.with(TagsTypes.Tags.ALBUM, albumName.getText()));
        tags.add(Pair.with(TagsTypes.Tags.LYRICS, lyricsField.getText()));
        try {
            idO.setTags(tags, regularExpr.getText());
        } catch (InvalidDataException | UnsupportedTagException | IOException | NotSupportedException e) {
            e.printStackTrace();
            showError(e);
        }

    }

    @FXML
    public void onMouseClickedOnList() {
        int index = mp3List.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }
        IDObject o = MP3FileList.get(index);
        ArrayList<Pair<TagsTypes.Tags, String>> Tags = null;
        try {
            Tags = o.getTags();
        } catch (InvalidDataException | UnsupportedTagException | IOException e) {
            e.printStackTrace();
            showError(e);
            return;
        }
        if(Tags.isEmpty()){
            showError("Une erreur est survenue");
            return;
        }
        for (Pair<TagsTypes.Tags, String> tag : Tags) {
            switch ((TagsTypes.Tags) tag.getValue(0)) {
                case ARTIST -> artistName.setText(tag.getValue1());
                case ALBUM -> albumName.setText(tag.getValue1());
                case TITLE -> trackName.setText(tag.getValue1());
                case YEAR -> year.setText(tag.getValue1());

            }
        }
        currentMessage = items.get(index);

    }

    public void onClose(ActionEvent actionEvent) {
        MainJFX.stopTray();
    }

    public void checkUpdate(){
        showTrayMessage("INOP");
        /*
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mise a jour");
        alert.setContentText("INOP");
        alert.showAndWait();
        return;
        */

        /*
        boolean error = false;
        try {
            InetAddress ip =InetAddress.getByName("www.github.com");
            if(!ip.isReachable(160)){
                error = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            error = true;
        }
        if(error){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mise a jour");
            alert.setContentText("Connexion indisponible");
            alert.showAndWait();
            return ;
        }
        Path tmpFile ;
        try {
            tmpFile = Files.createTempFile("temp",".ini");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            Files.copy(
                    //Some hardcoding never hurted anyone ...
                    new URL("https://raw.githubusercontent.com/FicheeSS/Projet6POO/c51af1d23221e97c8361219ae3dfe2cbc208daaf/config.ini").openStream(),
                    tmpFile,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Ini ini;
        try {
            ini = new Ini(new File(tmpFile.toString()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        assert ini != null;
        if(Main.MAJORVERSION < Integer.parseInt(ini.get("Application Properties", "MajorVersion"))||
                Main.VERSION < Integer.parseInt(ini.get("Application Properties", "Version"))||
                Main.BUILDNUMBER < Integer.parseInt(ini.get("Application Properties", "BuildNumber"))){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mise a jour");
            alert.setContentText("Une mise à jour est disponible");
            alert.showAndWait();
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mise a jour");
            alert.setContentText("Votre programme est à jour");
            alert.showAndWait();
        }

         */
    }

    public void onApplyOnFolder(ActionEvent actionEvent) {
        showTrayMessage("Starting batch apply...");
        applyBar.setProgress(0);
        for(var mp3f : MP3FileList){
            try {
                mp3f.setTags(mp3f.getTags(),regularExpr.getText());
                applyBar.setProgress((MP3FileList.indexOf(mp3f)/MP3FileList.size()) * 100);
            } catch (InvalidDataException | UnsupportedTagException | IOException | NotSupportedException e) {
                showError(e);
            }
        }
        showTrayMessage("Finished batch apply");
    }

    private record MessageObject(String text, File f) {

        public String getText() {
            return text;
        }

        public File getFile() {
            return this.f;
        }

    }
}
