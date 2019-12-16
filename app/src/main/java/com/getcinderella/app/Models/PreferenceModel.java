package com.getcinderella.app.Models;

public class PreferenceModel {
    public String m;
    public long t;

    public PreferenceModel( String m, long t) {
        this.m = m;
        this.t = t;
    }

    public PreferenceModel() {
    }


    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return "PreferenceModel{" +
                ", m='" + m + '\'' +
                ", t=" + t +
                '}';
    }
}
