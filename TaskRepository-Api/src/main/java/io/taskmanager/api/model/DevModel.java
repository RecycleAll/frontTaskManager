package io.taskmanager.api.model;

import io.taskmanager.test.Dev;

import java.time.LocalDate;

public class DevModel extends ObjectBaseModel<Dev>{

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private int githubId;

    public DevModel(int id, LocalDate updatedAt, LocalDate createdAt) {
        super(id, updatedAt, createdAt);
    }

    @Override
    public Dev convert() {
        return new Dev(id, firstname, lastname, email, password, githubId);
    }
}

