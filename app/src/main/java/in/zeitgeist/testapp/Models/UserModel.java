package in.zeitgeist.testapp.Models;

public class UserModel {
    private String Username;
    private long dp;

    public UserModel(String username, long dp) {
        Username = username;
        this.dp = dp;
    }
    public UserModel() {

    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public long getDp() {
        return dp;
    }

    public void setDp(long dp) {
        this.dp = dp;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "Username='" + Username + '\'' +
                ", dp=" + dp +
                '}';
    }
}
