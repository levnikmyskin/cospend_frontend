package it.edoardo.cospend_frontend;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CospendApp extends Application {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
}
