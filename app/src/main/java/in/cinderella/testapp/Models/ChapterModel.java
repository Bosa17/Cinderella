package in.cinderella.testapp.Models;

public class ChapterModel {
    public String name;
    public String desc;
    public long cost;

    public ChapterModel(String name, String desc, long cost) {
        this.name = name;
        this.desc = desc;
        this.cost = cost;
    }

    public ChapterModel() {
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


}
