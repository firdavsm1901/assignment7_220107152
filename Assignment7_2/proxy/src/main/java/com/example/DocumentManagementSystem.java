package com.example;

import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class Document {
    private String id;
    private String title;
    private String content;
    private String type;
    private Date uploadDate;

    public Document(String id, String title, String content, String type, Date uploadDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.uploadDate = uploadDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}


interface DocumentStorage {
    Document getDocument(String documentId, User user);
    List<Document> searchDocuments(String query, String searchType, User user);
    List<Document> searchDocumentsByType(String type, User user);
    List<Document> searchDocumentsByDate(Date startDate, Date endDate, User user);
}


class DocumentStorageImpl implements DocumentStorage {
    private Map<String, Document> documents = new HashMap<>();

    @Override
    public Document getDocument(String documentId, User user) {
        return documents.get(documentId);
    }

    @Override
    public List<Document> searchDocuments(String query, String searchType, User user) {
        switch (searchType.toLowerCase()) {
            case "content":
                return searchDocumentsByContent(query, user);
            case "type":
                return searchDocumentsByType(query, user);
            case "date":
                return searchDocumentsByDate(parseDate(query), parseDate(query), user);
            default:
                System.out.println("Invalid search type.");
                return Collections.emptyList();
        }
    }

    private List<Document> searchDocumentsByContent(String query, User user) {
        List<Document> results = new ArrayList<>();
        for (Document doc : documents.values()) {
            if (doc.getContent().contains(query)) {
                results.add(doc);
            }
        }
        return results;
    }

    @Override
    public List<Document> searchDocumentsByType(String type, User user) {
        List<Document> results = new ArrayList<>();
        for (Document doc : documents.values()) {
            if (doc.getType().equalsIgnoreCase(type)) {
                results.add(doc);
            }
        }
        return results;
    }

    @Override
    public List<Document> searchDocumentsByDate(Date startDate, Date endDate, User user) {
        List<Document> results = new ArrayList<>();
        for (Document doc : documents.values()) {
            if (doc.getUploadDate().after(startDate) && doc.getUploadDate().before(endDate)) {
                results.add(doc);
            }
        }
        return results;
    }

    public void addDocument(Document document) {
        documents.put(document.getId(), document);
    }

    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Using current date instead.");
            return new Date();
        }
    }
}

class User {
    private String username;
    private String passwordHash;
    private Set<String> roles;
    private boolean authenticated;

    public User(String username, String passwordHash, Set<String> roles) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean hasPermission(String permission) {
        return roles.contains(permission);
    }
}

class DocumentStorageProxy implements DocumentStorage {
    private DocumentStorage realStorage;

    public DocumentStorageProxy(DocumentStorage realStorage) {
        this.realStorage = realStorage;
    }

    @Override
    public Document getDocument(String documentId, User user) {
        return realStorage.getDocument(documentId, user);
    }

    @Override
    public List<Document> searchDocuments(String query, String searchType, User user) {
        return realStorage.searchDocuments(query, searchType, user);
    }

    @Override
    public List<Document> searchDocumentsByType(String type, User user) {
        return realStorage.searchDocumentsByType(type, user);
    }

    @Override
    public List<Document> searchDocumentsByDate(Date startDate, Date endDate, User user) {
        return realStorage.searchDocumentsByDate(startDate, endDate, user);
    }
}

public class DocumentManagementSystem {
    public static void main(String[] args) {
        DocumentStorageImpl realStorage = new DocumentStorageImpl();
        User user = authenticateUser();

        if (user != null) {
            DocumentStorage proxy = new DocumentStorageProxy(realStorage);
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;
            while (!exit) {
                displayOptions();
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1:
                        System.out.println();
                        System.out.print("Enter search type (content, type, or date): ");
                        String searchType = scanner.nextLine();
                        searchDocuments(proxy, searchType, user, scanner);
                        break;
                    case 2:
                        addDocuments(realStorage, user, scanner);
                        break;
                    case 3:
                        exit = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } else {
            System.out.println("Authentication failed.");
        }
    }

    private static User authenticateUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if ("user".equals(username) && "password".equals(password)) {
            User authenticatedUser = new User(username, "passwordHash", new HashSet<>(Arrays.asList("READ_DOCUMENT")));
            authenticatedUser.setAuthenticated(true);
            return authenticatedUser;
        }
        return null;
    }

    private static void addDocuments(DocumentStorageImpl realStorage, User user, Scanner scanner) {
        boolean uploadMore = true;
        while (uploadMore) {
            System.out.println();
            System.out.println("Upload Document:");
            System.out.print("ID: ");
            String id = scanner.nextLine();
            System.out.print("Title: ");
            String title = scanner.nextLine();
            System.out.print("Content: ");
            String content = scanner.nextLine();
            System.out.print("Type: ");
            String type = scanner.nextLine();
            
            boolean validDate = false;
            Date uploadDate = null;
            while (!validDate) {
                System.out.print("Upload Date (YYYY-MM-DD): ");
                String dateString = scanner.nextLine();
                uploadDate = parseDate(dateString);
                
                if (uploadDate != null) {
                    validDate = true;
                } else {
                    System.out.println("Invalid date format. Please enter the date in the format YYYY-MM-DD.");
                }
            }
    
            Document document = new Document(id, title, content, type, uploadDate);
            realStorage.addDocument(document);
            System.out.println();
            System.out.println("Document uploaded successfully.");
            System.out.println();
    
            System.out.print("Do you want to upload more documents? (yes/no): ");
            String response = scanner.nextLine();
            uploadMore = response.equalsIgnoreCase("yes");
            System.out.println();
        }
    }
    

    private static void searchDocuments(DocumentStorage proxy, String searchType, User user, Scanner scanner) {
        switch (searchType.toLowerCase()) {
            case "content":
                System.out.print("Enter content to search: ");
                String contentQuery = scanner.nextLine();
                List<Document> contentResults = proxy.searchDocuments(contentQuery, "content", user);
                displaySearchResults(contentResults);
                break;
            case "type":
                System.out.print("Enter type to search: ");
                String typeQuery = scanner.nextLine();
                List<Document> typeResults = proxy.searchDocuments(typeQuery, "type", user);
                displaySearchResults(typeResults);
                break;
            case "date":
                System.out.print("Enter start date (YYYY-MM-DD): ");
                String startDateInput = scanner.nextLine();
                System.out.print("Enter end date (YYYY-MM-DD): ");
                String endDateInput = scanner.nextLine();
                Date startDate = parseDate(startDateInput);
                Date endDate = parseDate(endDateInput);
                if (startDate != null && endDate != null) {
                    List<Document> dateResults = proxy.searchDocumentsByDate(startDate, endDate, user);
                    displaySearchResults(dateResults);
                } else {
                    System.out.println("Invalid date format.");
                }
                break;
            default:
                System.out.println("Invalid search criteria.");
        }
    }
    
    private static void displaySearchResults(List<Document> searchResults) {
        System.out.println();
        if (searchResults != null && !searchResults.isEmpty()) {
            System.out.println("Search results:");
            for (Document doc : searchResults) {
                System.out.println(doc.getTitle());
            }
        } else {
            System.out.println("No documents found.");
        }
        System.out.println();
    }
    
    private static Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Using current date instead.");
            return new Date();
        }
    }

    private static void displayOptions() {
        System.out.println();
        System.out.println("Options:");
        System.out.println("1. Search Documents");
        System.out.println("2. Upload Document");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }
}
