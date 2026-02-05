package com.university.administration.exceptions;

import java.util.UUID;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(UUID id) {
        super("Student not found with id: " + id);
    }
}

