package com.getcinderella.app.Models;

public class UserModel {
    private String a;
    private String fb_dp;
    private String quote;
    private String t;
    private String gender;
    private long charisma;
    private boolean isPremium;
    private long mask;
    private long pixies;

    public UserModel(String a, String fb_dp, String quote, String t, String gender, long charisma, boolean isPremium, long mask, long pixies) {
        this.a = a;
        this.fb_dp = fb_dp;
        this.quote = quote;
        this.t = t;
        this.gender = gender;
        this.charisma = charisma;
        this.isPremium = isPremium;
        this.mask = mask;
        this.pixies = pixies;
    }

    public UserModel() {
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getFb_dp() {
        return fb_dp;
    }

    public void setFb_dp(String fb_dp) {
        this.fb_dp = fb_dp;
    }

    public long getCharisma() {
        return charisma;
    }

    public void setCharisma(long charisma) {
        this.charisma = charisma;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }


    public long getMask() {
        return mask;
    }

    public void setMask(long mask) {
        this.mask = mask;
    }


    public long getPixies() {
        return pixies;
    }

    public void setPixies(long pixies) {
        this.pixies = pixies;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + a + '\'' +
                ", fb_dp='" + fb_dp + '\'' +
                ", quote='" + quote + '\'' +
                ", charisma=" + charisma +
                ", isPremium=" + isPremium +
                ", mask=" + mask +
                ", pixies=" + pixies +
                '}';
    }
}
