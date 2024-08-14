package ru.alexnika.faker.http.server.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FakeItem {
    private static final Logger logger = LogManager.getLogger(FakeItem.class.getName());
    private Long id;
    private String firstname;
    private String lastname;
    private String address;
    private String job;
    private String hobby;

    public FakeItem(Long id, String firstname, String lastname, String address,
                    String job, String hobby) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.job = job;
        this.hobby = hobby;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    @Override
    public String toString() {
        return "FakeItem{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", address='" + address + '\'' +
                ", job='" + job + '\'' +
                ", hobby='" + hobby + '\'' +
                '}';
    }
}
