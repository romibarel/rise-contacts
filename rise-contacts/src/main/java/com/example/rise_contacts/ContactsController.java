package com.example.rise_contacts;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ContactsController {
    private static final Logger logger = LoggerFactory.getLogger(ContactsController.class);
    final int CONTACTS_PER_PAGE = 10;
    final String FAILED_TO_LOAD_CONTACTS_MESSAGE = "failed to load contacts";

    @GetMapping("/contacts")
    public String getContacts() {
        List<Contact> contacts = DBManager.readContactsFromFile();
        if (contacts == null) {
            return FAILED_TO_LOAD_CONTACTS_MESSAGE;
        }
        logger.info("Conacts loaded successfully");
        return contacts.isEmpty() ? "Contacts book is empty." : getContactListString(contacts, 0);
    }

    @GetMapping("/contacts/{page}")
    public String getContacts(@PathVariable int page) {
        List<Contact> contacts = DBManager.readContactsFromFile();
        if (contacts == null) {
            return FAILED_TO_LOAD_CONTACTS_MESSAGE;
        }
        logger.info("Conacts loaded successfully");
        int lastPage = contacts.size() / CONTACTS_PER_PAGE;
        if (page<0 || page>lastPage) {
            logger.error("Attempted to request a page out of bounds.");
            return String.format("Page out of bounds, please select a page from 0 to %d.", lastPage);
        }
        return contacts.isEmpty() ? "Contacts book is empty." : getContactListString(contacts, page*CONTACTS_PER_PAGE);
    }

    @GetMapping("/contacts/search/{token}")
    public String searchContacts(@PathVariable String token) {
        List<Contact> contacts = DBManager.readContactsFromFile();
        if (contacts == null) {
            return FAILED_TO_LOAD_CONTACTS_MESSAGE;
        }
        List<Contact> matches = new LinkedList<>();
        for (Contact contact : contacts) {
            if (isContainsIgnoreCase(contact.getFirstName(), token)
                || isContainsIgnoreCase(contact.getLastName(), token)
                || isContainsIgnoreCase(contact.getPhone(), token)
                || isContainsIgnoreCase(contact.getAddress(), token)) {
                matches.add(contact);
            }
        }
        logger.info("Contact search success");
        return matches.isEmpty() ? String.format("No matches for %s.", token) : getContactListString(matches, 0);
    }

    @PostMapping("/contacts")
    public String addContact(@RequestBody Contact newContact) {
        List<Contact> contacts = DBManager.readContactsFromFile();
        int id = 0;
        if (contacts == null || contacts.isEmpty()) {
            contacts = new LinkedList<>();
        } else {
            id = contacts.get(contacts.size()-1).getId() + 1;
        }
        newContact.setId(id);
        contacts.add(newContact);
        if (!DBManager.rewriteContactsFile(contacts)) {
            String error = "Failed to add contact.";
            logger.error(error);
            return error;
        }
        logger.info("Adding new contact to contacts file success.");
        return String.format("Contact was successfully added to page %d. <br>%s", (contacts.size()-1)/CONTACTS_PER_PAGE, newContact.toString());
    }

    @PutMapping("/contacts/{id}")
    public String editContact(@PathVariable int id, @RequestBody Contact updatedContact) {
        List<Contact> contacts = DBManager.readContactsFromFile();
        if (contacts == null) {
            return FAILED_TO_LOAD_CONTACTS_MESSAGE;
        }
        Contact toEdit = getContactById(id, contacts);
        if (toEdit == null) {
            logger.error("Attempted to edit contact with invalid contact ID.");
            return String.format("User with ID %d was not found.", id);
        }

        toEdit.setFirstName(updatedContact.getFirstName()==null ? toEdit.getFirstName() : updatedContact.getFirstName());
        toEdit.setLastName(updatedContact.getLastName()==null ? toEdit.getLastName() : updatedContact.getLastName());
        toEdit.setPhone(updatedContact.getPhone()==null ? toEdit.getPhone() : updatedContact.getPhone());
        toEdit.setAddress(updatedContact.getAddress()==null ? toEdit.getAddress() : updatedContact.getAddress());
        if (!DBManager.rewriteContactsFile(contacts)) {
            String error = "Failed to edit contact.";
            logger.error(error);
            return error;
        }
        logger.info("Successfully edited contact with id "+id);
        return String.format("Contact with ID %d was updated:<br>%s", id, toEdit.toString());
    }

    @DeleteMapping("/contacts/{id}")
    public String deleteContact(@PathVariable int id) {
        List<Contact> contacts = DBManager.readContactsFromFile();
        if (contacts == null) {
            return FAILED_TO_LOAD_CONTACTS_MESSAGE;
        }
        Contact toDelete = getContactById(id, contacts);
        if (toDelete == null) {
            logger.error("Attempted to delete contact with invalid contact ID.");
            return String.format("User with ID %d was not found.", id);
        }
        contacts.remove(toDelete);
        if (!DBManager.rewriteContactsFile(contacts)) {
            String error = "Failed to delete contact.";
            logger.error(error);
            return error;
        }
        logger.info("Successfully deleted contact with id "+id);

        return String.format("Deleted contact %s.", toDelete.toString());
    }

    public int getContactsPerPage() {
        return CONTACTS_PER_PAGE;
    }

    private Contact getContactById(int id, List<Contact> contacts) {
        Contact answer = null;
        if (contacts == null) {
            return null;
        }
        for (Contact contact : contacts) {
            if (contact.getId()==id) {
                answer = contact;
                break;
            }
        }
        return answer;
    }

    private boolean isContainsIgnoreCase(String fullTerm, String searchTerm) {
        fullTerm = fullTerm.toLowerCase();
        searchTerm = searchTerm.toLowerCase();
        return fullTerm.contains(searchTerm);
    }

    private String getContactListString(List<Contact> contacts, int startIndex) {
        int counter = 0;
        String result = "";
        List<Contact> contactsSublist = contacts.subList(startIndex, contacts.size());
        for (Contact contact : contactsSublist) {
            result += (counter+startIndex+1) +". "+ contact.toString() + "<br>";
            counter++;
            if (counter==10) {
                break;
            }
        } 
        return result;
    }
}
