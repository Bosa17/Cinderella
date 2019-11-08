package com.getcinderella.app.Models;

public class PreferenceModel {
    public String p;
    public long t;

    public PreferenceModel(String p, long t) {
        this.p = p;
        this.t = t;
    }

    public PreferenceModel() {
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
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
                "p='" + p + '\'' +
                ", t=" + t +
                '}';
    }
}
