package com.example.sofrah_managment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Shift {
    private int id;
    private int employeeId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private LocalDateTime expectedCheckIn;
    private LocalDateTime expectedCheckOut;
    private boolean hasWorked;

    public Shift(int id, int employeeId, LocalDateTime checkIn, LocalDateTime checkOut,
                 LocalDateTime expectedCheckIn, LocalDateTime expectedCheckOut, boolean hasWorked) {
        this.id = id;
        this.employeeId = employeeId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.expectedCheckIn = expectedCheckIn;
        this.expectedCheckOut = expectedCheckOut;
        this.hasWorked = hasWorked;
    }

    public int getId() { return id; }
    public int getEmployeeId() { return employeeId; }
    public LocalDateTime getCheckIn() { return checkIn; }
    public LocalDateTime getCheckOut() { return checkOut; }
    public LocalDateTime getExpectedCheckIn() { return expectedCheckIn; }
    public LocalDateTime getExpectedCheckOut() { return expectedCheckOut; }
    public boolean isHasWorked() { return hasWorked; }

    public void setId(int id) { this.id = id; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }
    public void setExpectedCheckIn(LocalDateTime expectedCheckIn) { this.expectedCheckIn = expectedCheckIn; }
    public void setExpectedCheckOut(LocalDateTime expectedCheckOut) { this.expectedCheckOut = expectedCheckOut; }
    public void setHasWorked(boolean hasWorked) { this.hasWorked = hasWorked; }

    public String getCheckInFormatted() {
        return checkIn != null ? checkIn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "--";
    }

    public String getCheckOutFormatted() {
        return checkOut != null ? checkOut.toString() : "—";
    }

    public String getExpectedCheckInFormatted() {
        return expectedCheckIn != null ? expectedCheckIn.toString() : "—";
    }


}
