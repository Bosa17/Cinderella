package in.cinderella.testapp.Models;

import android.graphics.Bitmap;

public class RemoteUserConnection {
    public String remoteUserName;
    public long remoteUserKarma;
    public String remoteUserDp;

    public RemoteUserConnection(String remoteUserName, long remoteUserKarma, String remoteUserDp) {
        this.remoteUserName = remoteUserName;
        this.remoteUserKarma = remoteUserKarma;
        this.remoteUserDp = remoteUserDp;
    }

    public RemoteUserConnection() {
    }

    public String getRemoteUserName() {
        return remoteUserName;
    }

    public void setRemoteUserName(String remoteUserName) {
        this.remoteUserName = remoteUserName;
    }

    public long getRemoteUserKarma() {
        return remoteUserKarma;
    }

    public void setRemoteUserKarma(long remoteUserKarma) {
        this.remoteUserKarma = remoteUserKarma;
    }

    public String getRemoteUserDp() {
        return remoteUserDp;
    }

    public void setRemoteUserDp(String remoteUserDp) {
        this.remoteUserDp = remoteUserDp;
    }
}
