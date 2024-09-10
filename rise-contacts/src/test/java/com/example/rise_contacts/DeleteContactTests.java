package com.example.rise_contacts;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

public class DeleteContactTests {
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
    public void testDeleteContactSuccess() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701", 1);
        mockContacts.add(contact);
        dbManagerMock.when(() -> DBManager.getContactById(anyInt())).thenAnswer(invocation -> {
            return contact;
        });
        dbManagerMock.when(() -> DBManager.deleteContact(anyInt())).thenAnswer(invocation -> {
            mockContacts.remove(contact);
            return true;
        });
        String result = contactsController.deleteContact(0);
        assertEquals(String.format("Deleted contact %s.", contact.toString()), result);
        assertFalse(mockContacts.contains(contact));
    }

    @Test
    public void testDeleteContactIdNotFound() {
        dbManagerMock.when(() -> DBManager.getContactById(anyInt())).thenAnswer(invocation -> {
            return null;
        });
        String result = contactsController.deleteContact(1);
        assertEquals("User with ID 1 was not found.", result);
    }

    @Test
    public void testDeleteContactFail() {
        Contact contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701", 1);
        dbManagerMock.when(() -> DBManager.getContactById(anyInt())).thenAnswer(invocation -> {
            return contact;
        });
        dbManagerMock.when(() -> DBManager.deleteContact(anyInt())).thenAnswer(invocation -> {
            return false;
        });
        String result = contactsController.deleteContact(1);
        assertEquals("Failed to delete user with ID 1.", result);
    }
}
