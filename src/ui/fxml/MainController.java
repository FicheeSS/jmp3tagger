package ui.fxml;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import ui.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import org.javatuples.Pair;
import ui.IDObject;
import ui.MainJFX;
import ui.TagsTypes;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public TextField folder;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    public ListView<MessageObject> mp3List = new ListView<MessageObject>();
    public Button updFile;
    public Button searchLyrics;
    public TextArea lyricsField;
    private MessageObject currentMessage ;
    private final ObservableList<MessageObject> items = FXCollections.observableArrayList();
    private ArrayList<IDObject> MP3FileList = new ArrayList<IDObject>();
    public TextField artistName;
    public TextField trackName;
    public TextField year;
    public TextField albumName;
    public TextField regularExpr;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mp3List.setItems(items);
        mp3List.setCellFactory(param -> new ListCell<MessageObject>() {
            private final ImageView imageView = new ImageView();

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
        if(Main.lyrcisFetcher == null){
            searchLyrics.setDisable(true);
        }


    }
    protected void showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Erreur");
        //alert.setHeaderText("Erreur d'inscription dans la base de donnée");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public void addFileToDisp(File f){
        items.add(new MessageObject(f.getName() , f));
    }

    public void onSelectDirectory(ActionEvent actionEvent) {
        File selectedDirectory = directoryChooser.showDialog(MainJFX.Stage);
        if(selectedDirectory != null){
            folder.setText(selectedDirectory.getAbsolutePath());
        }else{
            return;
        }
        ArrayList<String> mp3Files = new ArrayList<String>();
        for(String f : selectedDirectory.list()){
            if(f.endsWith(".mp3")){
                 mp3Files.add(selectedDirectory.getAbsolutePath() + "\\"+ f) ;
            }
        }

        for(String s : mp3Files){
            File f = new File(s);
            addFileToDisp(f);
            IDObject o = new IDObject(f);
            MP3FileList.add(o);
        }

    }

    public void onSearchLyrics(ActionEvent actionEvent) {
        if(currentMessage == null){
            showError("No file selected");
            return;
        }
        lyricsField.setText(Main.lyrcisFetcher.GetLyrics(new IDObject(currentMessage.getFile())));
    }

    public void onUpdate(ActionEvent actionEvent) {
        IDObject idO = new IDObject(currentMessage.getFile());
        ArrayList<Pair<TagsTypes.Tags,String>> tags = new ArrayList<Pair<TagsTypes.Tags,String>>();


        tags.add(Pair.with(TagsTypes.Tags.ARTIST,artistName.getText()));
            tags.add(Pair.with(TagsTypes.Tags.TITLE,trackName.getText()));
            tags.add(Pair.with(TagsTypes.Tags.ALBUM,albumName.getText()));
            tags.add(Pair.with(TagsTypes.Tags.LYRICS,lyricsField.getText()));
        try {
            idO.setTags(tags,regularExpr.getText());
        } catch (InvalidDataException | UnsupportedTagException | IOException | NotSupportedException e) {
            e.printStackTrace();
        }

    }

    private class MessageObject {
        private final String text;
        private final File f ;
        public MessageObject(String text, File f) {
            this.text = text;
            this.f = f ;
        }

        public String getText() {
            return text;
        }
        public File getFile(){return this.f;}

    }
    @FXML
    public void onMouseClickedOnList(MouseEvent event) {
        int index = mp3List.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }
        IDObject o  = MP3FileList.get(index);
        ArrayList<Pair<TagsTypes.Tags,String>> Tags = o.getTags();
        for(Pair<TagsTypes.Tags,String> tag : Tags){
            switch ((TagsTypes.Tags)tag.getValue(0)){
                case ARTIST -> artistName.setText(tag.getValue1());
                case ALBUM -> albumName.setText(tag.getValue1());
                case TITLE -> trackName.setText(tag.getValue1());
                case YEAR -> year.setText(tag.getValue1());

            }
        }
        currentMessage = items.get(index);

    }
}
