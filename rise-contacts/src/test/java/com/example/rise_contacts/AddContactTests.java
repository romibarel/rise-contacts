package com.example.rise_contacts;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class AddContactTests {
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
    public void testAddContactSuccess() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact1 = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        Contact contact2 = new Contact("Jane", "Smith", "+1-555-987-6543", "456 Oak Avenue, Chicago, IL, 60610");
        mockContacts.add(contact1);
        when(DBManager.readContactsFromFile()).thenReturn(mockContacts);
        dbManagerMock.when(() -> DBManager.rewriteContactsFile(anyList())).thenAnswer(invocation -> {
            return true;
        });
        String result = contactsController.addContact(contact2);
        assertEquals("Contact was successfully added to page 0. <br>"+contact2.toString(), result);
        assertTrue(mockContacts.containsAll(List.of(contact1, contact2)));
        assertTrue(contact2.getId() > contact1.getId());
    }

    @Test
    public void testAddContactFirst() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact1 = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        when(DBManager.readContactsFromFile()).thenReturn(mockContacts);
        dbManagerMock.when(() -> DBManager.rewriteContactsFile(anyList())).thenAnswer(invocation -> {
            List<Contact> contacts = invocation.getArgument(0);
            mockContacts.clear();
            mockContacts.addAll(contacts);
            return true;
        });
        String result = contactsController.addContact(contact1);
        assertEquals("Contact was successfully added to page 0. <br>"+contact1.toString(), result);
        assertTrue(mockContacts.contains(contact1));
        assertTrue(contact1.getId() > -1);
    }

    @Test
    public void testAddContactNullContacts() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact1 = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        when(DBManager.readContactsFromFile()).thenReturn(null);
        dbManagerMock.when(() -> DBManager.rewriteContactsFile(anyList())).thenAnswer(invocation -> {
            List<Contact> contacts = invocation.getArgument(0);
            mockContacts.clear();
            mockContacts.addAll(contacts);
            return true;
        });
        String result = contactsController.addContact(contact1);
        assertEquals("Contact was successfully added to page 0. <br>"+contact1.toString(), result);
        assertTrue(mockContacts.contains(contact1));
        assertTrue(contact1.getId() > -1);
    }

    @Test
    public void testAddContactFailure() {
        Contact contact1 = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        when(DBManager.readContactsFromFile()).thenReturn(null);
        dbManagerMock.when(() -> DBManager.rewriteContactsFile(anyList())).thenAnswer(invocation -> {
            return false;
        });
        String result = contactsController.addContact(contact1);
        assertEquals("Failed to add contact.", result);
    }
}
