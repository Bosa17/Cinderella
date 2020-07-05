package com.getcinderella.app.Models;

public class PreferenceModel {
    public String m;
    public String id;

    public PreferenceModel( String m, String id) {
        this.m = m;
        this.id = id;
    }

    public PreferenceModel() {
    }


    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PreferenceModel{" +
                ", m='" + m + '\'' +
                ", id=" + id +
                '}';
    }
}
