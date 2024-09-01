package com.example.rise_contacts;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

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
        Contact contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        contact.setId(0);
        mockContacts.add(contact);
        when(DBManager.readContactsFromFile()).thenReturn(mockContacts);
        dbManagerMock.when(() -> DBManager.rewriteContactsFile(anyList())).thenAnswer(invocation -> {
            return true;
        });
        String result = contactsController.deleteContact(0);
        assertEquals(String.format("Deleted contact %s.", contact.toString()), result);
        assertFalse(mockContacts.contains(contact));
    }

    @Test
    public void testDeleteContactIdNotFound() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        contact.setId(0);
        mockContacts.add(contact);
        when(DBManager.readContactsFromFile()).thenReturn(mockContacts);
        dbManagerMock.when(() -> DBManager.rewriteContactsFile(anyList())).thenAnswer(invocation -> {
            return true;
        });
        String result = contactsController.deleteContact(1);
        assertEquals(String.format("User with ID 1 was not found.", contact.toString()), result);
    }

    @Test
    public void testDeleteContactFail() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        contact.setId(0);
        mockContacts.add(contact);
        when(DBManager.readContactsFromFile()).thenReturn(mockContacts);
        dbManagerMock.when(() -> DBManager.rewriteContactsFile(anyList())).thenAnswer(invocation -> {
            return false;
        });
        String result = contactsController.deleteContact(0);
        assertEquals(String.format("Failed to delete contact.", contact.toString()), result);
    }
}
