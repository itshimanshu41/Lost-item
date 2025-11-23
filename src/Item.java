import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Item {
    private final String id;
    private String title;          // short name, e.g., "Black Wallet"
    private String type;           // category, e.g., "Wallet", "Phone", "Key"
    private String description;    // more details
    private String location;       // where it was lost/found
    private LocalDate date;        // date reported
    private String reporterName;   // person who reported lost/found item
    private String contact;        // phone or email
    private boolean isFound;       // true if this record is a 'found' item
    private boolean resolved;      // true if matched & resolved

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public Item(String id, String title, String type, String description, String location,
                LocalDate date, String reporterName, String contact, boolean isFound, boolean resolved) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.description = description;
        this.location = location;
        this.date = date;
        this.reporterName = reporterName;
        this.contact = contact;
        this.isFound = isFound;
        this.resolved = resolved;
    }

    public Item(String title, String type, String description, String location,
                LocalDate date, String reporterName, String contact, boolean isFound) {
        this(UUID.randomUUID().toString(), title, type, description, location, date, reporterName, contact, isFound, false);
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public LocalDate getDate() { return date; }
    public String getReporterName() { return reporterName; }
    public String getContact() { return contact; }
    public boolean isFound() { return isFound; }
    public boolean isResolved() { return resolved; }

    public void setResolved(boolean resolved) { this.resolved = resolved; }

    // CSV serialization (safe simple escaping by replacing line breaks and commas)
    public String toCsvLine() {
        return escape(id) + "," + escape(title) + "," + escape(type) + "," +
               escape(description) + "," + escape(location) + "," + date.format(FMT) + "," +
               escape(reporterName) + "," + escape(contact) + "," + isFound + "," + resolved;
    }

    public static Item fromCsvLine(String line) {
        // naive CSV parse because we escape commas - works for this simple format
        String[] parts = splitCsv(line, 10);
        String id = unescape(parts[0]);
        String title = unescape(parts[1]);
        String type = unescape(parts[2]);
        String description = unescape(parts[3]);
        String location = unescape(parts[4]);
        LocalDate date = LocalDate.parse(parts[5], FMT);
        String reporterName = unescape(parts[6]);
        String contact = unescape(parts[7]);
        boolean isFound = Boolean.parseBoolean(parts[8]);
        boolean resolved = Boolean.parseBoolean(parts[9]);
        return new Item(id, title, type, description, location, date, reporterName, contact, isFound, resolved);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ").replace(",", "⹁"); // use unusual char for commas
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("⹁", ",");
    }

    private static String[] splitCsv(String line, int expected) {
        // our simple format: commas only appear replaced by special char so safe to split
        String[] parts = line.split(",", -1);
        if (parts.length != expected) {
            // attempt to pad to expected length to avoid crashes
            String[] padded = new String[expected];
            for (int i = 0; i < expected; i++) padded[i] = (i < parts.length ? parts[i] : "");
            return padded;
        }
        return parts;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s | Location: %s | Date: %s | Reporter: %s | Contact: %s | Found: %s | Resolved: %s",
                id, title, type, shortDesc(), location, date.format(FMT), reporterName, contact, isFound, resolved);
    }

    private String shortDesc() {
        if (description == null) return "";
        if (description.length() <= 40) return description;
        return description.substring(0, 37) + "...";
    }
}