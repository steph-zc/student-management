package com.studentmanagement;

public class DuplicateCourseNameException extends RuntimeException {

    public DuplicateCourseNameException(String courseName, Throwable cause) {
        super("Ja existe um curso com o nome: " + courseName, cause);
    }
}
