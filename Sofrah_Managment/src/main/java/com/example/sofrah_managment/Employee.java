package com.example.sofrah_managment;
import java.time.LocalDate;

public class Employee {
    private int id;
    private int jobTitleId;
    private LocalDate dateOfBirth;
    private String name;

    public Employee(int id, int jobTitleId, LocalDate dateOfBirth, String name) {
        this.id = id;
        this.jobTitleId = jobTitleId;
        this.dateOfBirth = dateOfBirth;
        this.name = name;
    }

    public Employee( int jobTitleId, LocalDate dateOfBirth, String name) {
        this.jobTitleId = jobTitleId;
        this.dateOfBirth = dateOfBirth;
        this.name = name;
    }

    public int getId() { return id; }
    public int getJobTitleId() { return jobTitleId; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getName() { return name; }

    public void setId(int id) { this.id = id; }
    public void setJobTitleId(int jobTitleId) { this.jobTitleId = jobTitleId; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setName(String name) { this.name = name; }

    public String toString() {
     return id + " - " + name;
    }


}
