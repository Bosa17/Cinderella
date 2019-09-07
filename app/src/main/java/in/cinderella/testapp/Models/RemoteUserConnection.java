package in.cinderella.testapp.Models;

public class RemoteUserConnection {
    private String remoteUserName;
    private long remoteUserSkill;
    private String remoteUserId;
    private String remoteUserDp;
    private String remoteUserQuote;


    public RemoteUserConnection() {
    }

    public String getRemoteUserId() {
        return remoteUserId;
    }

    public void setRemoteUserId(String remoteUserId) {
        this.remoteUserId = remoteUserId;
    }

    public String getRemoteUserQuote() {
        return remoteUserQuote;
    }

    public void setRemoteUserQuote(String remoteUserQuote) {
        this.remoteUserQuote = remoteUserQuote;
    }

    public String getRemoteUserName() {
        return remoteUserName;
    }

    public void setRemoteUserName(String remoteUserName) {
        this.remoteUserName = remoteUserName;
    }

    public long getRemoteUserSkill() {
        return remoteUserSkill;
    }

    public void setRemoteUserSkill(long remoteUserSkill) {
        this.remoteUserSkill = remoteUserSkill;
    }

    public String getRemoteUserDp() {
        return remoteUserDp;
    }

    public void setRemoteUserDp(String remoteUserDp) {
        this.remoteUserDp = remoteUserDp;
    }
}
