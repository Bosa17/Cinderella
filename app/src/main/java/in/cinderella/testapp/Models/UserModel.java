package in.cinderella.testapp.Models;

public class UserModel {
    private String username;
    private String fb_dp;
    private String quote;
    private long karma;
    private String gender;
    private long mask;
    private String fb_link;
    private long pixies;
//    private long age;


    public UserModel(String username, String fb_dp, String quote, long karma, String gender, long mask, String fb_link, long pixies) {
        this.username = username;
        this.fb_dp = fb_dp;
        this.quote = quote;
        this.karma = karma;
        this.gender = gender;
        this.mask = mask;
        this.fb_link = fb_link;
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

    public String getFb_link() {
        return fb_link;
    }

    public void setFb_link(String fb_link) {
        this.fb_link = fb_link;
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
                ", karma=" + karma +
                ", gender='" + gender + '\'' +
                ", mask=" + mask +
                ", fb_link='" + fb_link + '\'' +
                ", pixies=" + pixies +
                '}';
    }
}
