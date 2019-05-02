package in.cinderella.testapp.Models;

public class UserModel {
    private String username;
    private String fb_dp;
    private long karma;
    private String gender;
    private long mask;
//    private long age;


    public UserModel(String username, String fb_dp, long karma, String gender, long mask) {
        this.username = username;
        this.fb_dp = fb_dp;
        this.karma = karma;
        this.gender = gender;
        this.mask = mask;
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

    public long getMask() {
        return mask;
    }

    public void setMask(long mask) {
        this.mask = mask;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                ", fb_dp='" + fb_dp + '\'' +
                ", karma=" + karma +
                ", gender='" + gender + '\'' +
                ", mask=" + mask +
                '}';
    }
}
