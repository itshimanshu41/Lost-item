import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    private final Path filePath;

    public Repository(String filePath) {
        this.filePath = Paths.get(filePath);
        ensureFile();
    }

    private void ensureFile() {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent() == null ? Paths.get(".") : filePath.getParent());
                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    // header (for human)
                    writer.write("#id,title,type,description,location,date,reporterName,contact,isFound,resolved\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to create storage file: " + e.getMessage());
        }
    }

    public synchronized void saveItem(Item item) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {
            writer.write(item.toCsvLine());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving item: " + e.getMessage());
        }
    }

    public synchronized List<Item> loadAll() {
        List<Item> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                try {
                    Item it = Item.fromCsvLine(line);
                    list.add(it);
                } catch (Exception ex) {
                    System.err.println("Skipping bad line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading storage: " + e.getMessage());
        }
        return list;
    }

    public synchronized void saveAll(List<Item> items) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("#id,title,type,description,location,date,reporterName,contact,isFound,resolved\n");
            for (Item it : items) {
                writer.write(it.toCsvLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving items: " + e.getMessage());
        }
    }
}