package in.zeitgeist.testapp.Models;

public class PostModel {
    private String uid;
    private  String author;
    private  String title;
    private  String body;
    private  int Likes = 0;

    public PostModel() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public PostModel(String uid, String author, String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
    }


}
