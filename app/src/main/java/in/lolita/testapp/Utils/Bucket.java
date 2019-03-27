package in.lolita.testapp.Utils;

public class Bucket {

    private String name;
    private String firstImageContainedPath;

    public Bucket(String name, String firstImageContainedPath) {
        this.name = name;
        this.firstImageContainedPath = firstImageContainedPath;
    }

    public String getName() {
        return name;
    }

    public String getFirstImageContainedPath() {
        return firstImageContainedPath;
    }
}