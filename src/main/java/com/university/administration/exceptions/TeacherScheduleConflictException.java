package com.university.administration.exceptions;

public class TeacherScheduleConflictException extends RuntimeException {
    public TeacherScheduleConflictException(String message) {
        super(message);
    }
}

