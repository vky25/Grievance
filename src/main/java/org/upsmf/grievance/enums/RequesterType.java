package org.upsmf.grievance.enums;

public enum RequesterType {

    CANDIDATE(1), INSTITUTE(2), FACULTY(3), OTHERS(4), PUBLIC(5);

    private int id;
    RequesterType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
