package com.getcinderella.app.Models;

public class UserModel {
    private String username;
    private String fb_dp;
    private String quote;
    private long charisma;
    private boolean isPremium;
    private long mask;
    private long pixies;


    public UserModel(String username, String fb_dp, String quote, long charisma, boolean isPremium, long mask, long pixies) {
        this.username = username;
        this.fb_dp = fb_dp;
        this.quote = quote;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                ", fb_dp='" + fb_dp + '\'' +
                ", quote='" + quote + '\'' +
                ", charisma=" + charisma +
                ", isPremium=" + isPremium +
                ", mask=" + mask +
                ", pixies=" + pixies +
                '}';
    }
}
