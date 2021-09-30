package ui;

import java.io.IOException;

import static javafx.application.Application.launch;

public class Main {

    public static LyrcisFetcher lyrcisFetcher;

    public static void main(String[] args) {
        try {
            lyrcisFetcher = new LyrcisFetcher();
        } catch (IOException e) {
            e.printStackTrace();
            lyrcisFetcher = null;
        }

        launch(MainJFX.class, args);

    }


}
