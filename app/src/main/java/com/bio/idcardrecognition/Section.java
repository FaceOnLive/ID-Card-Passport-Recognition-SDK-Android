package com.bio.idcardrecognition;

import java.util.List;
import java.util.ArrayList;
public class Section {
    private String header;
    private List<Item> items;

    public Section(String header) {
        this.header = header;
        this.items = new ArrayList<>();
    }

    public String getHeader() {
        return header;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addItems(List<Item> newItems) {
        items.addAll(newItems);
    }
}

