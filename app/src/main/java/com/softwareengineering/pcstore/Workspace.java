package com.softwareengineering.pcstore;

import android.net.Uri;

public class Workspace {

    private String name ;
    int no ;
    Uri profile;

    public Workspace(String name, int no, Uri profile) {
        this.name = name;
        this.no = no;
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Uri getProfile() {
        return profile;
    }

    public void setProfile(Uri profile) {
        this.profile = profile;
    }
}
