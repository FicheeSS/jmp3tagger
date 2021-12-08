package ui.fxml;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import org.javatuples.Pair;
import ui.IDObject;
import ui.Main;
import ui.MainJFX;
import ui.TagsTypes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class MainController extends BaseControlleur {
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private final ObservableList<MessageObject> items = FXCollections.observableArrayList();
    public TextField folder;
    public ListView<MessageObject> mp3List = new ListView<>();
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
    private File selectedDirectory;
    private final ArrayList<IDObject> MP3FileList = new ArrayList<>();
    public CheckBox addLyrics;
    @FXML
    public Button folderApplyB;
    @FXML
    private CheckBox recursage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mp3List.setItems(items);
        regularExpr.setText(Main.BASEREGEX);
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
            addLyrics.setDisable(true);
        }


    }



    public void addFileToDisp(File f) {
        items.add(new MessageObject(f.getName(), f));
    }

    /**
     * Research the directory
     */
    public void refreshDirectory(){
        ArrayList<String> mp3Files = new ArrayList<>();
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
        // Complete garbage never do that please
        } else if(folder.getText() == null || Objects.equals(folder.getText(), "")){
            return;
        }else{
            selectedDirectory = new File(folder.getText());
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
            showError(e);
        }
    }

    public void onUpdate() {
        IDObject idO = new IDObject(currentMessage.getFile());
        ArrayList<Pair<TagsTypes.Tags, String>> tags = new ArrayList<>();
        tags.add(Pair.with(TagsTypes.Tags.ARTIST, artistName.getText()));
        tags.add(Pair.with(TagsTypes.Tags.TITLE, trackName.getText()));
        tags.add(Pair.with(TagsTypes.Tags.ALBUM, albumName.getText()));
        tags.add(Pair.with(TagsTypes.Tags.LYRICS, lyricsField.getText()));
        try {
            idO.setTags(tags, regularExpr.getText());
        } catch (InvalidDataException | UnsupportedTagException | IOException | NotSupportedException e) {
            e.printStackTrace();
            showTrayMessage(e.getMessage());
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
        ArrayList<Pair<TagsTypes.Tags, String>> Tags;
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

    public void onClose() {
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

    public void onApplyOnFolder() {
        Main.AM.BatchApply(applyBar,MP3FileList,addLyrics.isSelected(),regularExpr.getText());
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
