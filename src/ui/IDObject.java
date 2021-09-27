package ui;

import com.mpatric.mp3agic.*;
import org.javatuples.Pair;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class IDObject {

    private final File Mp3File;

    public IDObject(File f){
        this.Mp3File = f ;
        
    }
    public ArrayList<Pair<TagsTypes.Tags,String>> getTags(){
        com.mpatric.mp3agic.Mp3File mp3file = null;
        ArrayList<Pair<TagsTypes.Tags,String>> tags = new ArrayList<Pair<TagsTypes.Tags,String>>();
        {
            try {
                mp3file = new Mp3File(Mp3File.getAbsoluteFile());
            } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                e.printStackTrace();
                return tags;
            }
        }
        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();

            tags.add(Pair.with(TagsTypes.Tags.ARTIST,id3v2Tag.getArtist()));
            tags.add(Pair.with(TagsTypes.Tags.TITLE,id3v2Tag.getTitle()));
            tags.add(Pair.with(TagsTypes.Tags.ALBUM,id3v2Tag.getAlbum()));
            tags.add(Pair.with(TagsTypes.Tags.LYRICS,id3v2Tag.getLyrics()));
        }
        return tags;
    }
    public void setTags(ArrayList<Pair<TagsTypes.Tags,String>> tags , String regularExp) throws InvalidDataException, UnsupportedTagException, IOException, NotSupportedException {
        Mp3File mp3file = new Mp3File(this.Mp3File.getAbsoluteFile());
        ID3v2 id3v2Tag = null;
        id3v2Tag = mp3file.getId3v2Tag();
        assert (id3v2Tag != null);
        for(Pair<TagsTypes.Tags,String> tag : tags){
            switch ((TagsTypes.Tags)tag.getValue(0)){
                case ARTIST -> id3v2Tag.setArtist(tag.getValue1());
                case ALBUM -> id3v2Tag.setAlbum(tag.getValue1());
                case TITLE ->  id3v2Tag.setTitle(tag.getValue1());
                case YEAR ->  id3v2Tag.setYear(tag.getValue1());

            }
        }
        regularExp = regularExp.replaceAll("%artist%" , id3v2Tag.getArtist());
        regularExp = regularExp.replaceAll("%album%" , id3v2Tag.getAlbum());
        regularExp = regularExp.replaceAll("%trackname%" , id3v2Tag.getTitle());
        regularExp = regularExp.replaceAll("%year%" , id3v2Tag.getYear());
        mp3file.save(Mp3File.getParentFile().getPath() + "\\" + regularExp + ".mp3");
    }
}