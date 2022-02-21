package net.app.civilix;

/**
 * Created by Belal on 9/30/2017.
 */

public class Project {
    int id;
    String name, type, joiningDate;
    double estimate;

    public Project(int id, String name, String type, String joiningDate, double estimate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.joiningDate = joiningDate;
        this.estimate = estimate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public double getEstimate() {
        return estimate;
    }
}
