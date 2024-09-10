package com.example.rise_contacts;

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
        List<Contact> contacts = DBManager.readContacts(0, CONTACTS_PER_PAGE);
        if (contacts == null) {
            return FAILED_TO_LOAD_CONTACTS_MESSAGE;
        }
        logger.info("Conacts loaded successfully");
        return contacts.isEmpty() ? "Contacts book is empty." : getContactListString(contacts, 0);
    }

    @GetMapping("/contacts/{page}")
    public String getContacts(@PathVariable int page) {
        List<Contact> contacts = DBManager.readContacts(page, CONTACTS_PER_PAGE);
        if (contacts == null) {
            return FAILED_TO_LOAD_CONTACTS_MESSAGE;
        }
        logger.info("Conacts loaded successfully");
        if (page<0 || (page > 0 && contacts.isEmpty())) {
            int lastPage = DBManager.getNumOfContacts() / CONTACTS_PER_PAGE;
            if (lastPage <0) {
                logger.error("Failed to get total number of contacts");
                return "Page out of bounds.";
            }
            logger.error("Attempted to request a page out of bounds.");
            return String.format("Page out of bounds, please select a page from 0 to %d.", lastPage);
        }
        return contacts.isEmpty() ? "Contacts book is empty." : getContactListString(contacts, page);
    }

    @GetMapping("/contacts/search/{token}")
    public String searchContacts(@PathVariable String token) {
        List<Contact> matches = DBManager.getMatches(token, CONTACTS_PER_PAGE);
        if (matches == null) {
            return FAILED_TO_LOAD_CONTACTS_MESSAGE;
        }
        logger.info("Contact search success");
        return matches.isEmpty() ? String.format("No matches for %s.", token) : getContactListString(matches, 0);
    }

    @PostMapping("/contacts")
    public String addContact(@RequestBody Contact newContact) {
        int id = DBManager.addContact(newContact);
        if (id < 0) {
            String error = "Failed to add contact.";
            logger.error(error);
            return error;
        }
        newContact.setId(id);
        logger.info("Adding new contact to contacts file success.");
        return String.format("Contact was successfully added to page %d. <br>%s", (DBManager.getNumOfContacts()-1)/CONTACTS_PER_PAGE, newContact.toString());
    }

    @PutMapping("/contacts/{id}")
    public String editContact(@PathVariable int id, @RequestBody Contact updatedContact) {
        Contact toEdit = DBManager.getContactById(id);
        if (toEdit == null) {
            logger.error("Attempted to edit contact with invalid contact ID.");
            return String.format("User with ID %d was not found.", id);
        }

        toEdit.setFirstName(updatedContact.getFirstName()==null ? toEdit.getFirstName() : updatedContact.getFirstName());
        toEdit.setLastName(updatedContact.getLastName()==null ? toEdit.getLastName() : updatedContact.getLastName());
        toEdit.setPhone(updatedContact.getPhone()==null ? toEdit.getPhone() : updatedContact.getPhone());
        toEdit.setAddress(updatedContact.getAddress()==null ? toEdit.getAddress() : updatedContact.getAddress());
        if (!DBManager.editContact(id, toEdit)) {
            logger.error("Failed to edit contact.");
            return String.format("Failed to edit contact with ID %d.", id);
        }
        logger.info("Successfully edited contact with id "+id);
        return String.format("Contact with ID %d was updated:<br>%s", id, toEdit.toString());
    }

    @DeleteMapping("/contacts/{id}")
    public String deleteContact(@PathVariable int id) {
        Contact toDelete = DBManager.getContactById(id);
        if (toDelete == null) {
            logger.error("Attempted to delete contact with invalid contact ID.");
            return String.format("User with ID %d was not found.", id);
        }
        if (!DBManager.deleteContact(id)) {
            String error = String.format("Failed to delete user with ID %d.", id);
            logger.error(error);
            return String.format(error);
        }
        logger.info(String.format("Successfully deleted contact with id %d.", id));
        return String.format("Deleted contact %s.", toDelete.toString());
    }

    @DeleteMapping("/contacts/reset")
    public String resetContacts() {
        String message = "Failed to reset contacts.";
        if (!DBManager.resetContacts()) {
            logger.error(message);
            return message;
        }
        message = "Contacts reset successfully.";
        logger.info(message);
        return message;
    }

    public int getContactsPerPage() {
        return CONTACTS_PER_PAGE;
    }

    private String getContactListString(List<Contact> contacts, int page) {
        int counter = page*CONTACTS_PER_PAGE+1;
        String result = "";
        for (Contact contact : contacts) {
            result += counter +". "+ contact.toString() + "<br>";
            counter++;
        } 
        return result;
    }
}
