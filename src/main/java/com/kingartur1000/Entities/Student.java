package com.kingartur1000.Entities;

public class Student {
    private String fullName;
    private Group group;
    private int attendanceCount;

    public Student(String fullName, Group group) {
        this.fullName = fullName;
        this.group = group;
        this.attendanceCount = 0;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Group getGroup() {
        return group;
    }

    public String getGroupName() {
        return group != null ? group.getName() : "";
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getAttendanceCount() {
        return attendanceCount;
    }

    public void incrementAttendance() {
        attendanceCount++;
    }

    public void setAttendanceCount(int count) {
        this.attendanceCount = count;
    }
}