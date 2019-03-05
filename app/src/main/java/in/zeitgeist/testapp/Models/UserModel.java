package in.zeitgeist.testapp.Models;

public class UserModel {
    private String username;
    private long dp;

    public UserModel(String username, long dp) {
        this.username = username;
        this.dp = dp;
    }
    public UserModel() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
                "Username='" + username + '\'' +
                ", dp=" + dp +
                '}';
    }
}
