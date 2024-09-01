package com.example.rise_contacts;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DBManager {
    private final static String FILEPATH = "/app/src/main/resources/contacts.json";
    private static final Logger logger = LoggerFactory.getLogger(DBManager.class);

    public static List<Contact> readContactsFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File(FILEPATH);
            if (!file.exists()) {
                logger.error("Contacts.json file not found at: " + file.getAbsolutePath());
                return null;
            }
            if (file.length() == 0) {
                logger.warn("Contacts.json file is empty");
                return new LinkedList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<Contact>>() {});
        } catch (IOException e) {
            logger.error("Error reading contacts", e.getMessage(), e);
            return null;
        }
    }

    public static boolean rewriteContactsFile(List<Contact> contacts) {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File(FILEPATH);
            if (!file.exists()) {
                logger.warn("Contacts file not found, attempting to create.");
                file.createNewFile();
                // Can't be false because the file doesn't exist. IO errors thrown will be caught in catch clause
                logger.info("Contacts file created successfully.");
            }
            objectMapper.writeValue(file, contacts);
        } catch (IOException e) {
            logger.error("Error writing to contacts file: " + e.getMessage(), e);
            return false;
        }
        return true;
    }
}
