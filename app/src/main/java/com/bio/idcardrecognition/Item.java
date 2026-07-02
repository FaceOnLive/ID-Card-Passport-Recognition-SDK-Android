package com.bio.idcardrecognition;

public class Item {
    private String title;
    private String content;

    public Item(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
