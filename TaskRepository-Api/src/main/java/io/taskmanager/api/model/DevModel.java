package io.taskmanager.api.model;

import io.taskmanager.test.Dev;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DevModel extends ObjectBaseModel<Dev>{

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private int githubId;

    public DevModel(int id, LocalDateTime updatedAt, LocalDateTime createdAt) {
        super(id, updatedAt, createdAt);
    }

    @Override
    public Dev convert() {
        Dev dev = new Dev(id, firstname, lastname, email, password, githubId);
        dev.setUpdatedAt(updatedAt);
        return dev;
    }
}

