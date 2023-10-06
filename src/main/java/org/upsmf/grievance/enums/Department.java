package org.upsmf.grievance.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Department {

    REGISTRATION(1, "Registration"), AFFILIATION(2, "Affiliation"), ASSESSMENT(3, "Assessment"), EXAMS(4, "Exams"), OTHERS(-1, "Others");

    private int id;
    private String code;
    Department(int id) {
        this.id = id;
    }

    Department(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public static List<Department> getById(int id) {
       return Arrays.stream(Department.values()).filter(x -> x.id == id).collect(Collectors.toList());
    }

    public static List<Department> getByCode(String code) {
        return Arrays.stream(Department.values()).filter(x -> x.code.equals(code)).collect(Collectors.toList());
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
