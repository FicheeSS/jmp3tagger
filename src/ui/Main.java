package ui;

import java.io.IOException;

import static javafx.application.Application.launch;

public class Main {

    public static LyrcisFetcher lyrcisFetcher;
    public static void main(String[] args){
        try {
            lyrcisFetcher = new LyrcisFetcher();
        } catch(IOException e ){
            e.printStackTrace();
            lyrcisFetcher = null;
        }

        launch(MainJFX.class,args);
        /*
        Mp3File mp3file = null;
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println(s);

        {
            try {
                mp3file = new Mp3File(s+ "/file.mp3");
            } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                e.printStackTrace();
                return;
            }
        }
        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            System.out.println("Artist: " + id3v2Tag.getArtist());
            System.out.println("Title: " + id3v2Tag.getTitle());
            System.out.println("Album: " + id3v2Tag.getAlbum());
            System.out.println("Year: " + id3v2Tag.getYear());
            System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
            System.out.println("Lyrics: " + id3v2Tag.getLyrics());
            byte[] albumImageData = id3v2Tag.getAlbumImage();
            if (albumImageData != null) {
                System.out.println("Have album image data, length: " + albumImageData.length + " bytes");
                System.out.println("Album image mime type: " + id3v2Tag.getAlbumImageMimeType());
            }
        }

         */

    }


}
