package com.ia.ia_base.models;

public class Location {
    private int id;
    private String schoolSector;
    private String roomNumber;

    public Location(){}

    public Location(int id, String schoolSector, String roomNumber) {
        this.id = id;
        this.schoolSector = schoolSector;
        this.roomNumber = roomNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSchoolSector() {
        return schoolSector;
    }

    public void setSchoolSector(String schoolSector) {
        this.schoolSector = schoolSector;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", schoolSector='" + schoolSector + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                '}';
    }
}
