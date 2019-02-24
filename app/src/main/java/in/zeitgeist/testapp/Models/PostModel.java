package in.zeitgeist.testapp.Models;

public class PostModel {

    private long VotesFor;
    private long VotesAgainst;
    private String  Uid;
    private String Title;
    private String PostID;

    public PostModel(long votesFor, long votesAgainst, String uid, String title, String postID) {
        VotesFor = votesFor;
        VotesAgainst = votesAgainst;
        Uid = uid;
        Title = title;
        PostID = postID;
    }

    public PostModel(){

    }

    public long getVotesFor() {
        return VotesFor;
    }

    public void setVotesFor(long votesFor) {
        VotesFor = votesFor;
    }

    public long getVotesAgainst() {
        return VotesAgainst;
    }

    public void setVotesAgainst(long votesAgainst) {
        VotesAgainst = votesAgainst;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPostID() {
        return PostID;
    }

    public void setPostID(String postID) {
        PostID = postID;
    }

    @Override
    public String toString() {
        return "PostModel{" +
                "VotesFor=" + VotesFor +
                ", VotesAgainst=" + VotesAgainst +
                ", Uid='" + Uid + '\'' +
                ", Title='" + Title + '\'' +
                ", PostID='" + PostID + '\'' +
                '}';
    }
}
