package com.szn.jukebox.model;

import java.util.List;



public class SubMenuItem {

    int id; // id | pos
    String name;
    boolean active;
    boolean isChild;
    int resId;      // Ref au picto
    List<SubMenuItem> childs;

    public SubMenuItem(int id, String name) {
        this.id = id;
        this.name = name;
        this.active = true;
    }

    public SubMenuItem(int id, String name, boolean act, boolean child) {
        this.id = id;
        this.name = name;
        this.active = act;
        this.isChild = child;
    }

    public SubMenuItem(int id, String name, boolean active, boolean isChild, int resId, List<SubMenuItem> childs) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.isChild = isChild;
        this.resId = resId;
        this.childs = childs;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setIsChild(boolean isChild) {
        this.isChild = isChild;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public List<SubMenuItem> getChilds() {
        return childs;
    }

    public void setChilds(List<SubMenuItem> childs) {
        this.childs = childs;
    }
}
