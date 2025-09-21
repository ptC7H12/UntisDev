package com.ptc7h12.untisservice;

public class UntisRequest {
    private String username;
    private String password;
    private String server;
    private String schoolName;
    private Integer days; // Optional: Anzahl Tage

    // Constructors
    public UntisRequest() {}

    // Getters und Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getServer() { return server; }
    public void setServer(String server) { this.server = server; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
}