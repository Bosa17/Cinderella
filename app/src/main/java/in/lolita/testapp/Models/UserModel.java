package in.lolita.testapp.Models;

public class UserModel {
    private String username;
    private String fb_dp;
    private long karma;
    private String gender;
    private long profile_pic;
//    private long age;

    public UserModel(String username, String fb_dp, long karma, String gender, long profile_pic) {
        this.username = username;
        this.fb_dp = fb_dp;
        this.karma = karma;
        this.gender = gender;
        this.profile_pic = profile_pic;
    }

    public UserModel() {
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

    public long getKarma() {
        return karma;
    }

    public void setKarma(long karma) {
        this.karma = karma;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(long profile_pic) {
        this.profile_pic = profile_pic;
    }
}
