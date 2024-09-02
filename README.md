# Welcome to Contacts!

This program provides RESTful API to maintain a local contacts book, similar to the one we can find in our phones, using Java Spring Boot.
There are two main classes in this project:
1. `ContactsController.java` - contains the endpoints for getting, searching, adding, editting, and deleting contacts.
2. `DBManager.java` - communicates with the database, which is a json file called `contacts.json` that can be found in the resources directory.


## endpoints:

**1. Get contacts:**

  **Endpoint**: `GET /contacts`

  **Description:** Returns the first page of the paginated contacts book.

  **Response:**

  - If contacts are loaded successfully:
      - Returns a string representing the list of contacts in the first page.
  - If no contacts are found:
      - Returns "Contacts book is empty."
  - If there is a failure loading contacts:
      - Returns "failed to load contacts"

  Example Response:

    1. John Doe, +1-555-123-4567, 123 Elm Street, Springfield, IL, 62701
    2. Jane Smith, +1-555-987-6543, 456 Oak Avenue, Chicago, IL, 60610

**2. Get Contacts by Page**

  **Endpoint:** `GET /contacts/{page}`

  **Description:** Retrieves contacts for a specific page. The first page is 0.

  **Parameters:** page (int) - The page number to retrieve.

  **Response:**

  - If page is within bounds and contacts are found:
      - Returns a list of contacts numerated according to the page number.
  - If the page number is out of bounds:
      - Returns a message indicating the page is out of bounds.
  - If no contacts are found:
      - Returns "Contacts book is empty."
  - If there is a failure loading contacts:
      - Returns "failed to load contacts"

  Example Response for page 2:

    21. John Doe, +1-555-123-4567, 123 Elm Street, Springfield, IL, 62701
    22. Jane Smith, +1-555-987-6543, 456 Oak Avenue, Chicago, IL, 60610

**3. Search Contacts**

  **Endpoint:** `GET /contacts/search/{token}`

  **Description:** Searches for contacts that match the provided search token. 
  The search is case-insensitive and checks the first name, last name, phone, and address fields.

  **Parameters:** token (String) - The search token.

  **Response:**

  - If matches are found:
      - Returns a list of matching contacts formatted as HTML.
  - If no matches are found:
      - Returns "No matches for [token]."
  - If there is a failure loading contacts:
      - Returns "failed to load contacts"

**4. Add Contact**

  **Endpoint:** `POST /contacts`

  **Description:** Adds a new contact to the database. If the DB JSON file doesn't exist or is misplaced, this function will create the file in the expected location.

  **Body:** A JSON representation of the Contact object to be added. Fields may be null or empty.
  
  Example:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1-555-123-4567",
  "address": "123 Elm Street, Springfield, IL, 62701"
}
```
  **Response:**

  - If the contact is added successfully:
      - Returns a success message with the page number and contact details.
  - If there is a failure adding the contact:
      - Returns "Failed to add contact."

**5. Edit Contact**

  **Endpoint:** `PUT /contacts/{id}`

  **Description:** Updates an existing contact with the provided ID.

  **Parameters:** id (int) - The ID of the contact to be updated.
  
  **Body:** A JSON representation of the Contact object with the updated fields.
      For example, this JSON will only change the phone number of the contact:
```json
{
    "phone" : "+1-555-123-4567"
}
```

  **Response:**

  - If the contact is updated successfully:
      - Returns a success message with the updated contact details.
  - If the contact with the specified ID is not found:
      - Returns "User with ID [id] was not found."
  - If there is a failure updating the contact:
      - Returns "Failed to edit contact."

**6. Delete Contact**

  **Endpoint:** `DELETE /contacts/{id}`

  **Description:** Deletes the contact with the specified ID.

  **Parameters:** id (int) - The ID of the contact to be deleted.

  **Response:**

  - If the contact is deleted successfully:
      - Returns a success message with the deleted contact details.
  - If the contact with the specified ID is not found:
      - Returns "User with ID [id] was not found."
  - If there is a failure deleting the contact:
      - Returns "Failed to delete contact."


## Setup and Installation:
1. Clone the Repository: 
`git clone https://github.com/your-repo/contacts-management-api.git
cd contacts-management-api` .
2. Navigate to the root directory with `cd rise-contacts/rise-contacts` . 
3. Build the Docker Image:
`docker build -t rise-contacts .`
4. Run the Docker Container:
`docker run -v path/to/resources/dir:/app/src/main/resources -p 8080:8080 rise-contacts`.
   - Replace `path/to/resources/dir` with your (full) path to the resources dir.
   - Example: `docker run -v C:/Users/Owner/Desktop/rise-contacts3/rise-contacts/src/main/resources:/app/src/main/resources -p 8080:8080 rise-contacts`.
5. Access the application at `http://localhost:8080`.
You can use tools like Postman to interact with the API endpoints.
