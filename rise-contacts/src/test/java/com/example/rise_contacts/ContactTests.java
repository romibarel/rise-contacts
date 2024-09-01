package com.example.rise_contacts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ContactTests {

    private Contact contact;

    @Test
    public void testConstructor() {
        contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        assertEquals("John", contact.getFirstName());
        assertEquals("Doe", contact.getLastName());
        assertEquals("+1-555-123-4567", contact.getPhone());
        assertEquals("123 Elm Street, Springfield, IL, 62701", contact.getAddress());
        assertEquals(-1, contact.getId());
    }

    @Test
    public void testToStringFullFields() {
        contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        String expected = "John Doe: +1-555-123-4567, 123 Elm Street, Springfield, IL, 62701. id: -1";
        assertEquals(expected, contact.toString());
    }

    @Test
    public void testToStringNullFields() {
        contact = new Contact(null, null, null, null);
        String expected = "Missing name: Missing phone number, Missing address. id: -1";
        assertEquals(expected, contact.toString());
    }

    @Test
    public void testToStringBlankFields() {
        contact = new Contact("", " ", "    ", "");
        String expected = "Missing name: Missing phone number, Missing address. id: -1";
        assertEquals(expected, contact.toString());
    }

    @Test
    public void testToStringSomeBlankAndNullFields() {
        contact = new Contact("John", null, "+1-555-123-4567", "");
        String expected = "John: +1-555-123-4567, Missing address. id: -1";
        assertEquals(expected, contact.toString());
    }
}
