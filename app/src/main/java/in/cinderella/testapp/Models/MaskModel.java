package in.cinderella.testapp.Models;

public class MaskModel {
    public long mask;
    public long cost;
    public boolean isUnlocked;

    public MaskModel(long mask, long cost, boolean isUnlocked) {
        this.mask = mask;
        this.cost = cost;
        this.isUnlocked = isUnlocked;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public long getMask() {
        return mask;
    }

    public void setMask(long mask) {
        this.mask = mask;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
}
