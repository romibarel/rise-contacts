package com.example.rise_contacts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

public class SearchContactsTests {
    
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
    public void testSearchContactSuccess() {
        List<Contact> mockContacts = new ArrayList<>();
        Contact contact1 = new Contact("John", "Doe", "+1-555-123-4567", "123 Elm Street, Springfield, IL, 62701");
        Contact contact2 = new Contact("Jane", "Smith", "+1-555-987-6543", "456 Oak Avenue, Chicago, IL, 60610");
        mockContacts.add(contact1);
        mockContacts.add(contact2);
        dbManagerMock.when(() -> DBManager.getMatches(anyString(), anyInt())).thenAnswer(invocation -> {
            return mockContacts;
        });
        String result = contactsController.searchContacts("j");
        String expected = "1. "+ contact1.toString() + "<br>"+
                            "2. "+contact2.toString() + "<br>";
        assertEquals(expected, result);
        dbManagerMock.when(() -> DBManager.getMatches(anyString(), anyInt())).thenAnswer(invocation -> {
            return List.of(contact1);
        });
        result = contactsController.searchContacts("123");
        expected = "1. "+ contact1.toString() + "<br>";
        assertEquals(expected, result);
    }

    @Test
    public void testSearchContactNotFound() {
        dbManagerMock.when(() -> DBManager.getMatches(anyString(), anyInt())).thenAnswer(invocation -> {
            return new LinkedList<>();
        });
        String result = contactsController.searchContacts("jane");
        assertEquals("No matches for jane.", result);
    }

    @Test
    public void testSearchContactFailure() {
        dbManagerMock.when(() -> DBManager.getMatches(anyString(), anyInt())).thenAnswer(invocation -> {
            return null;
        });
        String result = contactsController.searchContacts("jane");
        assertEquals("failed to load contacts", result);
    }
}
