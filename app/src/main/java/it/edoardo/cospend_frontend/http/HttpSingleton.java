package it.edoardo.cospend_frontend.http;

import okhttp3.OkHttpClient;

public class HttpSingleton {

    private static HttpSingleton instance;
    public final OkHttpClient client;
    private HttpSingleton() {
        this.client = new OkHttpClient();
    }

    public static HttpSingleton getInstance() {
       if (instance == null) {
           instance = new HttpSingleton();
       }
       return instance;
    }

}