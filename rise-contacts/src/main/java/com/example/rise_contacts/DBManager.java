package com.example.rise_contacts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBManager {
    private final static String FILEPATH = "jdbc:sqlite:src/main/resources/Contacts.db";
    private static final Logger logger = LoggerFactory.getLogger(DBManager.class);
    private static final int TIMEOUT = 10;

    public static List<Contact> readContacts(int page, int contactPerPage) {
        List<Contact> contacts = null;
        try 
        (
            Connection connection = DriverManager.getConnection(FILEPATH);
        ) {
            String query = "SELECT * FROM contacts LIMIT ? OFFSET ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, contactPerPage);
            statement.setInt(2, page*contactPerPage);
            statement.setQueryTimeout(TIMEOUT);
            ResultSet rs = statement.executeQuery();
            contacts = new LinkedList<>();
            while (rs.next()) {
                contacts.add(mapResultToContact(rs));
            }
            connection.close();
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return contacts;
    } 

    public static Contact getContactById(int id) {
        Contact contact = null;
        try 
        (
            Connection connection = DriverManager.getConnection(FILEPATH);
        ) {
            String query = "SELECT * FROM contacts WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setQueryTimeout(TIMEOUT);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                contact = mapResultToContact(rs);
            }
            connection.close();
            return contact;
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return contact;
    }

    public static List<Contact> getMatches(String term, int contactPerPage) {
        List<Contact> contacts = null;
        try 
        (
            Connection connection = DriverManager.getConnection(FILEPATH);
        ) {
            String query = 
                "SELECT * FROM contacts "+
                "WHERE first_name LIKE ?"+
                "OR last_name LIKE ?"+
                "OR phone LIKE ?"+
                "OR address LIKE ?"+
                " LIMIT ?";
            PreparedStatement statement = connection.prepareStatement(query);
            term = "%" + term + "%";
            statement.setString(1, term);
            statement.setString(2, term);
            statement.setString(3, term);
            statement.setString(4, term);
            statement.setInt(5, contactPerPage);
            statement.setQueryTimeout(TIMEOUT);
            ResultSet rs = statement.executeQuery();
            contacts = new LinkedList<>();
            while (rs.next()) {
                contacts.add(mapResultToContact(rs));
            }
            connection.close();
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }

        return contacts;
    }

    public static int addContact(Contact newContact) {
        int id = -1;
        try 
        (
            Connection connection = DriverManager.getConnection(FILEPATH);
        ) {
            ResultSet tables = connection.getMetaData().getTables(null, null, "contacts", null);
            if (!tables.next() && !initDB(connection)) {
                logger.error("Failed to create the contacts table to add a contact.");
                return id;
            }
            String query = 
                "INSERT INTO contacts (first_name, last_name, phone, address) "+
                "VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newContact.getFirstName());
            statement.setString(2, newContact.getLastName());
            statement.setString(3, newContact.getPhone());
            statement.setString(4, newContact.getAddress());
            statement.setQueryTimeout(TIMEOUT);
            if (statement.executeUpdate() > 0) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
            connection.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return id;
    }

    public static boolean editContact(int id, Contact updatedContact) {
        boolean success = false;
        try 
        (
            Connection connection = DriverManager.getConnection(FILEPATH);
        ) {
            String query = 
                "UPDATE contacts SET " +
                "first_name = ?," +
                "last_name = ?," +
                "phone = ?," +
                "address = ?" +
                "WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, updatedContact.getFirstName());
            statement.setString(2, updatedContact.getLastName());
            statement.setString(3, updatedContact.getPhone());
            statement.setString(4, updatedContact.getAddress());
            statement.setInt(5, id);
            statement.setQueryTimeout(TIMEOUT);
            if (statement.executeUpdate() > 0) {
                success = true;
            }
            connection.close();
            return success;
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return success;
    }

    public static boolean deleteContact(int id) {
        boolean success = false;
        try 
        (
            Connection connection = DriverManager.getConnection(FILEPATH);
        ) {
            String query = "DELETE FROM contacts WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setQueryTimeout(TIMEOUT);
            if (statement.executeUpdate() > 0) {
                success = true;
            }
            connection.close();
            return success;
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return success;
    }

    public static boolean resetContacts() {
        boolean success = false;
        try 
        (
            Connection connection = DriverManager.getConnection(FILEPATH);
            Statement statement = connection.createStatement();
        ) {
            statement.setQueryTimeout(TIMEOUT);
            statement.executeUpdate("DROP TABLE contacts");
            success = initDB(connection);
            connection.close();
            return success;
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return success;
    }

    public static int getNumOfContacts() {
        try (
            Connection connection = DriverManager.getConnection(FILEPATH);
            Statement statement = connection.createStatement();
        ) {
            statement.setQueryTimeout(TIMEOUT);
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS total_rows FROM contacts");
            if (rs.next()) {
                return rs.getInt("total_rows");
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return -1;
    }

    private static Contact mapResultToContact(ResultSet rs) throws SQLException {
        return new Contact
        (
            rs.getString("first_name"), 
            rs.getString("last_name"), 
            rs.getString("phone"), 
            rs.getString("address"),
            rs.getInt("id")
        );
    }

    private static boolean initDB(Connection connection) {
        String createTableQuery = "CREATE TABLE contacts (" +
                                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    "first_name TEXT, " +
                                    "last_name TEXT, " +
                                    "phone TEXT, " +
                                    "address TEXT);";
        try {
            Statement createTableStatement = connection.createStatement();
            createTableStatement.execute(createTableQuery);
            return true;
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }
}
