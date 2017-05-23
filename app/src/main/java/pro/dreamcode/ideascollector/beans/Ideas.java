package pro.dreamcode.ideascollector.beans;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by migue on 25/04/2017.
 */

public class Ideas extends RealmObject{

    private String description;
    @PrimaryKey
    private long initialTime;
    private long plannedTime;
    private boolean completed;

    public Ideas() {
    }

    public Ideas(String description, long initialTime, long plannedTime, boolean completed) {
        this.setDescription(description);
        this.setInitialTime(initialTime);
        this.setPlannedTime(plannedTime);
        this.setCompleted(completed);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getInitialTime() {
        return initialTime;
    }

    private void setInitialTime(long initialTime) {
        this.initialTime = initialTime;
    }

    public long getPlannedTime() {
        return plannedTime;
    }

    public void setPlannedTime(long plannedTime) {
        this.plannedTime = plannedTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
