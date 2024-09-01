package com.example.rise_contacts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Contact {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private int id;

    @JsonCreator
    public Contact(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("phone") String phone,
            @JsonProperty("address") String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.id = -1;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String firstNamePresented = getOrDefaultString(firstName, "");
        String lastNamePresented = getOrDefaultString(lastName, "");

        if (firstNamePresented.isBlank() && lastNamePresented.isBlank()) {
            firstNamePresented = "Missing name";
        }
        
        return String.format("%s%s%s: %s, %s. id: %d", 
        firstNamePresented, 
        lastNamePresented.equals("") ? "":" ", 
        lastNamePresented, 
        getOrDefaultString(phone, "Missing phone number"), 
        getOrDefaultString(address, "Missing address"), 
        id);
    }

    private String getOrDefaultString(String string, String fallback) {
        if (string == null || string.isBlank()) {
            return fallback;
        }
        return string;
    }
}
