package ui;

import com.mpatric.mp3agic.*;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class IDObject  implements Runnable{

    private boolean isLyrics =false;
    private final File Mp3File;
    private String regex;
    private LyrcisFetcher lf;
    public IDObject(File f) {
        this.Mp3File = f;

    }

    /**
     * Return an formated tab of the Tags name and what contain the tags
     * @return Array of tuple
     * @throws InvalidDataException triggered if the tag containt invalidData
     * @throws UnsupportedTagException triggered if the tag retrieved is not recognized
     * @throws IOException triggered if the file is not accessible
     */
    public ArrayList<Pair<TagsTypes.Tags, String>> getTags() throws InvalidDataException, UnsupportedTagException, IOException {
        com.mpatric.mp3agic.Mp3File mp3file;
        ArrayList<Pair<TagsTypes.Tags, String>> tags = new ArrayList<Pair<TagsTypes.Tags, String>>();

        mp3file = new Mp3File(Mp3File.getAbsoluteFile());


        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();

            tags.add(Pair.with(TagsTypes.Tags.ARTIST, (id3v2Tag.getArtist() == null) ? "" : id3v2Tag.getArtist() ));
            tags.add(Pair.with(TagsTypes.Tags.TITLE, (id3v2Tag.getTitle() == null ) ? "" : id3v2Tag.getTitle()));
            tags.add(Pair.with(TagsTypes.Tags.ALBUM, (id3v2Tag.getAlbum() == null) ? "" : id3v2Tag.getAlbum() ));
            tags.add(Pair.with(TagsTypes.Tags.LYRICS, (id3v2Tag.getLyrics() == null) ? "" : id3v2Tag.getLyrics() ));
        }
        return tags;
    }

    /**
     * Update the tag from the modified array and replace the data in the regular exp
     * @param tags Array of tuple
     * @param regularExp A regular expressions that contain specific field
     * @throws InvalidDataException triggered if the tag containt invalidData
     * @throws UnsupportedTagException triggered if the tag retrieved is not recognized
     * @throws IOException triggered if the file is not accessible or cannot be written
     * @throws NotSupportedException ?
     */

    public void setTagsLyrics(ArrayList<Pair<TagsTypes.Tags, String>> tags, String regularExp,LyrcisFetcher lf) throws InvalidDataException, UnsupportedTagException, IOException, NotSupportedException {
        String artist = "", title = "",albumName = "";
        for(var tag : tags){
            switch ((TagsTypes.Tags) tag.getValue(0)) {
                case ARTIST -> artist = tag.getValue1();
                case TITLE -> title  = tag.getValue1();
                case ALBUM -> albumName = tag.getValue1();
            }
        }
        tags.clear();
        tags.add(Pair.with(TagsTypes.Tags.ARTIST, artist));
        tags.add(Pair.with(TagsTypes.Tags.TITLE,title));
        tags.add(Pair.with(TagsTypes.Tags.ALBUM, albumName));
        tags.add(Pair.with(TagsTypes.Tags.LYRICS, lf.GetLyricsFromArtistNameAndTrackName(artist,title)));
        setTags(tags,regularExp);

    }

    public void setTags(ArrayList<Pair<TagsTypes.Tags, String>> tags, String regularExp) throws InvalidDataException, UnsupportedTagException, IOException, NotSupportedException {
        Mp3File mp3file = new Mp3File(this.Mp3File.getAbsoluteFile());
        ID3v2 id3v2Tag;
        id3v2Tag = mp3file.getId3v2Tag();
        assert (id3v2Tag != null);
        for (Pair<TagsTypes.Tags, String> tag : tags) {
            switch ((TagsTypes.Tags) tag.getValue(0)) {
                case ARTIST -> id3v2Tag.setArtist(tag.getValue1());
                case ALBUM -> id3v2Tag.setAlbum(tag.getValue1());
                case TITLE -> id3v2Tag.setTitle(tag.getValue1());
                case YEAR -> id3v2Tag.setYear(tag.getValue1());

            }
        }
        if (id3v2Tag.getArtist() != null && !id3v2Tag.getArtist().isEmpty()) {
            regularExp = regularExp.replaceAll("%artist%", id3v2Tag.getArtist());
        }
        if (id3v2Tag.getAlbum() != null && !id3v2Tag.getAlbum().isEmpty()) {
            regularExp = regularExp.replaceAll("%album%", id3v2Tag.getAlbum());
        }
        if (id3v2Tag.getTitle() != null && !id3v2Tag.getTitle().isEmpty()) {
            regularExp = regularExp.replaceAll("%trackname%", id3v2Tag.getTitle());
        }
        if (id3v2Tag.getYear()!= null && !id3v2Tag.getYear().isEmpty()) {
            regularExp = regularExp.replaceAll("%year%", id3v2Tag.getYear());
        }
        int i =0 ; //Make sure rename to already existing mp3 file before creating it
        while(new File(Mp3File.getParentFile().getPath() + "\\" + regularExp + i + ".mp3").exists()) {
            if(i>0) {
                mp3file.save(Mp3File.getParentFile().getPath() + "\\" + regularExp + i + ".mp3");
            }else{
                mp3file.save(Mp3File.getParentFile().getPath() + "\\" + regularExp  + ".mp3");

            }
        }
        //Mp3File.delete();
    }

    /**
     * Sets the outside variable before running the thread
     * @param lyrics : Boolean if lyrics need to be search
     * @param reg : String regular expression of the .mp3 filename
     * @param lf : LyricsFetcher the LyricsFetcher used to dll the lyrics; if null will not search
     */
    public void beforeRun(boolean lyrics, String reg, @Nullable LyrcisFetcher lf ){
        if(lyrics && lf == null ) {
            lyrics = false;
        }
        isLyrics = lyrics;
        regex = reg;
        this.lf = lf;
    }

    @Override
    public void run() {
        /*
        try {
            Thread.sleep(new Random().nextInt(5000) + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         */
        if(isLyrics){
            try {
                setTagsLyrics(this.getTags(),regex,lf);
            } catch (InvalidDataException | UnsupportedTagException | IOException | NotSupportedException e) {
                e.printStackTrace();
            }
        }else{
            try {
                setTags(this.getTags(),regex);
            } catch (InvalidDataException | UnsupportedTagException | IOException | NotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

}
