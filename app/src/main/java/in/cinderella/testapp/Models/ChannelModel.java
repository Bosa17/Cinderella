package in.cinderella.testapp.Models;

public class ChannelModel {
    public String name;
    public String option0;
    public String option1;
    public String option2;
    public long cost;

    public ChannelModel(String name, String option0, String option1, String option2, long cost) {
        this.name = name;
        this.option0 = option0;
        this.option1 = option1;
        this.option2 = option2;
        this.cost = cost;
    }

    public ChannelModel() {
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOption0() {
        return option0;
    }

    public void setOption0(String option0) {
        this.option0 = option0;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "ChannelModel{" +
                ", name='" + name + '\'' +
                ", option0='" + option0 + '\'' +
                ", option1='" + option1 + '\'' +
                ", option2='" + option2 + '\'' +
                '}';
    }
}
