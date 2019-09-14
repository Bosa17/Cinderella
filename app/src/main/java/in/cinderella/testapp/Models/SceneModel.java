package in.cinderella.testapp.Models;

import java.io.Serializable;

public class SceneModel implements Serializable {
    public String name;
    public String desc;
    public String option0;
    public String option1;

    public SceneModel(String name, String desc, String option0, String option1) {
        this.name = name;
        this.desc = desc;
        this.option0 = option0;
        this.option1 = option1;
    }

    public SceneModel() {
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption0() {
        return option0;
    }

    public void setOption0(String option0) {
        this.option0 = option0;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "SceneModel{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", option0='" + option0 + '\'' +
                ", option1='" + option1 + '\'' +
                '}';
    }
}
