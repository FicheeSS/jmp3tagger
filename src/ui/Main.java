package ui;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

import static javafx.application.Application.launch;

public class Main {

    public static int MAJORVERSION  ;
    public static LyrcisFetcher lyrcisFetcher;
    public static int VERSION;
    public static int BUILDNUMBER;
    public static String BASEREGEX ;

    public static void main(String[] args) {
        try {
            lyrcisFetcher = new LyrcisFetcher();
        } catch (IOException e) {
            e.printStackTrace();
            lyrcisFetcher = null;
        }
        Ini ini = null;
        try {
            ini = new Ini(new File("config.ini"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        MAJORVERSION = Integer.parseInt(ini.get("Application Properties", "MajorVersion"));
        VERSION = Integer.parseInt(ini.get("Application Properties", "Version"));
        BUILDNUMBER = Integer.parseInt(ini.get("Application Properties", "BuildNumber"));
        BASEREGEX = ini.get("Last", "LastRegex");
        launch(MainJFX.class, args);

    }



}
