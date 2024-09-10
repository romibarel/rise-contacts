package com.example.rise_contacts;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

public class EditContactTests {
    private MockedStatic<DBManager> dbManagerMock;
    private ContactsController contactsController;

    @BeforeEach
    public void setUp() {
        dbManagerMock = mockStatic(DBManager.class);
        contactsController = new ContactsController();
    }

    @AfterEach
    public void tearDown() {
        dbManagerMock.close();
    }

    @Test
    public void testEditContactChangeAllFields() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701", 1);
        mockContacts.add(contact);
        dbManagerMock.when(() -> DBManager.getContactById(anyInt())).thenAnswer(invocation -> {
            return contact;
        });
        dbManagerMock.when(() -> DBManager.editContact(anyInt(), any(Contact.class))).thenAnswer(invocation -> {
            return true;
        });
        String result = contactsController.editContact(1, new Contact("Jane", "Smith", "+1-555-987-6543", "456 Oak Avenue, Chicago, IL, 60610"));
        assertEquals("Contact with ID 1 was updated:<br>"+contact.toString(), result);
        assertEquals(contact.getFirstName(), "Jane");
        assertEquals(contact.getLastName(), "Smith");
        assertEquals(contact.getPhone(), "+1-555-987-6543");
        assertEquals(contact.getAddress(), "456 Oak Avenue, Chicago, IL, 60610");
        assertEquals(contact.getId(), 1);
        assertTrue(mockContacts.contains(contact));
    }

    @Test
    public void testEditContactDeleteFields() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701", 1);
        mockContacts.add(contact);
        dbManagerMock.when(() -> DBManager.getContactById(anyInt())).thenAnswer(invocation -> {
            return contact;
        });
        dbManagerMock.when(() -> DBManager.editContact(anyInt(), any(Contact.class))).thenAnswer(invocation -> {
            return true;
        });
        String result = contactsController.editContact(1, new Contact("", "", "", ""));
        assertEquals("Contact with ID 1 was updated:<br>"+contact.toString(), result);
        assertEquals(contact.getFirstName(), "");
        assertEquals(contact.getLastName(), "");
        assertEquals(contact.getPhone(), "");
        assertEquals(contact.getAddress(), "");
        assertEquals(contact.getId(), 1);
        assertTrue(mockContacts.contains(contact));
    }

    @Test
    public void testEditContactEditSomeFields() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701", 1);
        mockContacts.add(contact);
        dbManagerMock.when(() -> DBManager.getContactById(anyInt())).thenAnswer(invocation -> {
            return contact;
        });
        dbManagerMock.when(() -> DBManager.editContact(anyInt(), any(Contact.class))).thenAnswer(invocation -> {
            return true;
        });
        String result = contactsController.editContact(1, new Contact("", null, null, null));
        assertEquals("Contact with ID 1 was updated:<br>"+contact.toString(), result);
        assertEquals(contact.getFirstName(), "");
        assertEquals(contact.getLastName(), "Doe");
        assertEquals(contact.getPhone(), "+1-555-123-4567");
        assertEquals(contact.getAddress(), "123 Elm Street, Springfield, IL, 62701");
        assertEquals(contact.getId(), 1);
        assertTrue(mockContacts.contains(contact));
    }

    @Test
    public void testEditContactIdNotFound() {
        dbManagerMock.when(() -> DBManager.getContactById(anyInt())).thenAnswer(invocation -> {
            return null;
        });
        String result = contactsController.editContact(1, new Contact("", null, null, null));
        assertEquals("User with ID 1 was not found.", result);
    }

    @Test
    public void testEditContactFail() {
        Contact contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701", 1);
        dbManagerMock.when(() -> DBManager.getContactById(anyInt())).thenAnswer(invocation -> {
            return contact;
        });
        dbManagerMock.when(() -> DBManager.editContact(anyInt(), any(Contact.class))).thenAnswer(invocation -> {
            return false;
        });
        String result = contactsController.editContact(1, new Contact("", null, null, null));
        assertEquals("Failed to edit contact with ID 1.", result);
    }
}
