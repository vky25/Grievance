package org.upsmf.grievance.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Department {

    REGISTRATION(1), EXAM(2), HALL_TICKET(3), OTHERS(-1);

    private int id;
    Department(int id) {
        this.id = id;
    }

    public static List<Department> getById(int id) {
       return Arrays.stream(Department.values()).filter(x -> x.id == id).collect(Collectors.toList());
    }
}
