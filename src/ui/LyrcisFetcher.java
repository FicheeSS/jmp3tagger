package ui;

import org.javatuples.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class LyrcisFetcher {

    public LyrcisFetcher() throws IOException {
            InetAddress ip =InetAddress.getByName("genius.com");
            if(!ip.isReachable(160)){
                throw new IOException("Site unreachable");
            }

    }

    public String GetLyrics(IDObject idO) {
        String artist = null, trackName = null;
        ArrayList<Pair<TagsTypes.Tags, String>> Tags = idO.getTags();
        for (Pair<TagsTypes.Tags, String> tag : Tags) {
            switch ((TagsTypes.Tags) tag.getValue(0)) {
                case ARTIST -> artist = tag.getValue1();
                case TITLE -> trackName = tag.getValue1();

            }
        }
        return GetLyricsFromArtistNameAndTrackName(artist,trackName);
    }
    private String GetLyricsFromArtistNameAndTrackName(String artistname, String trackname){
        String URL = CreateURL(artistname, trackname);// Generate the correct URl to get the html
        Document doc;

        try {
            doc = Jsoup.connect(URL).get();// get the html code
        } catch (IOException ex) {
            System.err.println("Could not get lyrics for " + artistname + " /w track name " + trackname + " /w the URL : " + URL);
            return "";

        }
        String FinalLyrics = PrepareHTML(doc);// remove all the junk from the html to get a great lyrics text
        if (FinalLyrics.isEmpty()) {
            FinalLyrics = "[Instrumental]";// If the track is empty we are assuming that the track is instrumental even if this case normally never occur
        }
        return FinalLyrics;
    }

    private  String FormatName(String TrackName) {
        TrackName = TrackName.replace("(Bonus Track)", "").replaceAll("/", "-").replace("ö", "o").replaceAll("\\s+", "-").replace(",", "").replace("'", "").replace(".", "").replace(" ", "-").toLowerCase(); // fell free to add your own special caracter / words

        if (String.valueOf(TrackName.charAt(TrackName.length() - 1)).contains("-")) {
            return TrackName.substring(0, TrackName.length() - 1); //remove doubled up -- at the end because of the add of the -lyrics in the URL creator with can occur when their is a space at the end of the track
        }

        return TrackName;
    }

    private  String CreateURL(String artistname, String trackname) {
        String TrackName = FormatName(trackname);
        String ArtistName = FormatName(artistname);
        String url;
        url = "https://genius.com/" + ArtistName + "-" + TrackName + "-lyrics";//regular expression for the url

        return url;
    }

    private  String PrepareHTML(Document doc) {
        String lyrics = doc.select("div.lyrics").toString();
        lyrics = lyrics.replaceAll("<br>", "/n");
        String FinalLyrics = Jsoup.parse(lyrics).text();//Not the most elegant way but whatever it works like a charm
        FinalLyrics = FinalLyrics.replaceAll("/n", "\n");
        return FinalLyrics;
    }
    
}
