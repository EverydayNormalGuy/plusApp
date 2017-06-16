package com.plusapp.pocketbiceps.app.database;

/**
 * Created by Metin on 24.03.2015.
 */

public class MyMarkerObj {
    private long id;
    private String title;
    private String snippet;
    private String position;
    private long timestamp;
    private int counter;
    private String path;

    public MyMarkerObj(){

    }

    public MyMarkerObj(long id, String title, String snippet, String position, long timestamp, int counter, String path) {
        this.setId(id);
        this.setTitle(title);
        this.setSnippet(snippet);
        this.setPosition(position);
        this.setTimestamp(timestamp);
        this.setCounter(counter);
    }

    public MyMarkerObj(String title, String snippet, String position, long timestamp, int counter, String path){

        this.setTitle(title);
        this.setSnippet(snippet);
        this.setPosition(position);
        this.setTimestamp(timestamp);
        this.setCounter(counter);
        this.setPath(path);


    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getSnippet() {
        return snippet;
    }
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public long getTimestamp(){
        return timestamp;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
    public int getCounter(){
        return counter;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getPath(){
        return path;
    }
}
