package ui;

import javafx.scene.control.ProgressBar;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApplicationManager {

    public void BatchApply(ProgressBar applyBar,ArrayList<IDObject> MP3FileList,boolean isLyrics,String regularExpr){
        MainJFX.trayIcon.showInfoMessage("Starting batch apply...");
        applyBar.setProgress(0);
        ArrayList<Future> threadArrayList = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(25);
        for(var mp3f : MP3FileList){
            mp3f.beforeRun(isLyrics,regularExpr, Main.lyrcisFetcher);
            Thread t = new Thread(mp3f);
            threadArrayList.add(executor.submit(t));
        }

        Thread gt = new Thread(() -> {
            float nbT = threadArrayList.size();
            ArrayList<Future> tbd = new ArrayList<>();
            while (!threadArrayList.isEmpty()) {
                for (var t : threadArrayList) {
                    if (t.isDone()) {
                        tbd.add(t);
                    }
                }
                for (var t : tbd) {
                    threadArrayList.remove(t);
                }
                tbd.clear();

                applyBar.setProgress(((nbT - threadArrayList.size()) / nbT));

            }
            MainJFX.trayIcon.showInfoMessage("Finished batch apply");
        });
        gt.start();
    }
}
