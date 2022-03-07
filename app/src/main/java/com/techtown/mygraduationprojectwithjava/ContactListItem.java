package com.techtown.mygraduationprojectwithjava;

import java.util.HashMap;

public class ContactListItem {
    private String objectId;
    private String name;
    private String time;
    private String image;

    public ContactListItem() {
    }

    public ContactListItem(String name, String time, String image) {
        this.name = name;
        this.time = time;
        this.image = image;
    }

    public ContactListItem(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public ContactListItem(String objectId, String name, String time, String image) {
        this.objectId = objectId;
        this.name = name;
        this.time = time;
        this.image = image;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
