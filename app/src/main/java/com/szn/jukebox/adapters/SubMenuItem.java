package com.szn.jukebox.adapters;

/**
 * Created by Julien Sezn on 22/09/2015.
 *
 */
public class SubMenuItem {

    int id;
    String name;

    public SubMenuItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
