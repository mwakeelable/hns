package com.linked_sys.hns.Model;

public class Staff {
    private String title;
    private String name;
    private int id;

    public Staff(String title, String name, int id) {
        this.title = title;
        this.name = name;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Staff) {
            Staff s = (Staff) obj;
            if (s.getTitle().equals(title) && s.getTitle() == title) return true;
        }

        return false;
    }
}
