package in.cinderella.testapp.Models;

public class RemoteUserConnection {
    private String remoteUserName;
    private long remoteUserCharisma;
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

    public long getRemoteUserCharisma() {
        return remoteUserCharisma;
    }

    public void setRemoteUserCharisma(long remoteUserCharisma) {
        this.remoteUserCharisma = remoteUserCharisma;
    }

    public String getRemoteUserDp() {
        return remoteUserDp;
    }

    public void setRemoteUserDp(String remoteUserDp) {
        this.remoteUserDp = remoteUserDp;
    }
}
