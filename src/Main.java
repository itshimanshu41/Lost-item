import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String STORAGE = "data/items.csv";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) {
        Repository repo = new Repository(STORAGE);
        LostAndFoundService service = new LostAndFoundService(repo);
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Lost & Found Portal (Console) ===");
        loop:
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    handleReport(sc, service, false); // lost
                    break;
                case "2":
                    handleReport(sc, service, true); // found
                    break;
                case "3":
                    listAll(service);
                    break;
                case "4":
                    searchById(sc, service);
                    break;
                case "5":
                    tryMatches(sc, service);
                    break;
                case "6":
                    resolveItem(sc, service);
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    break loop;
                default:
                    System.out.println("Unknown option. Try again.");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\nChoose an action:");
        System.out.println("1) Report LOST item");
        System.out.println("2) Report FOUND item");
        System.out.println("3) List all items");
        System.out.println("4) View item by ID");
        System.out.println("5) Find potential matches for an item (by ID)");
        System.out.println("6) Mark item as resolved (found/returned)");
        System.out.println("0) Exit");
        System.out.print("Select: ");
    }

    private static void handleReport(Scanner sc, LostAndFoundService svc, boolean isFound) {
        System.out.println(isFound ? "\n--- Report FOUND item ---" : "\n--- Report LOST item ---");
        System.out.print("Title (short): ");
        String title = sc.nextLine().trim();
        System.out.print("Type (category): ");
        String type = sc.nextLine().trim();
        System.out.print("Description (details): ");
        String desc = sc.nextLine().trim();
        System.out.print("Location (where lost/found): ");
        String location = sc.nextLine().trim();
        System.out.print("Date (yyyy-mm-dd) [leave blank for today]: ");
        String dateRaw = sc.nextLine().trim();
        LocalDate date = dateRaw.isEmpty() ? LocalDate.now() : LocalDate.parse(dateRaw, FMT);
        System.out.print("Your name: ");
        String name = sc.nextLine().trim();
        System.out.print("Contact (phone/email): ");
        String contact = sc.nextLine().trim();

        Item item = isFound
                ? svc.reportFound(title, type, desc, location, date, name, contact)
                : svc.reportLost(title, type, desc, location, date, name, contact);

        System.out.println("Reported successfully. Item ID: " + item.getId());
    }

    private static void listAll(LostAndFoundService svc) {
        List<Item> items = svc.listAll();
        if (items.isEmpty()) {
            System.out.println("No items reported yet.");
            return;
        }
        System.out.println("\n--- All items ---");
        items.forEach(i -> System.out.println(i));
    }

    private static void searchById(Scanner sc, LostAndFoundService svc) {
        System.out.print("Enter item ID: ");
        String id = sc.nextLine().trim();
        svc.findById(id).ifPresentOrElse(
                i -> System.out.println(i),
                () -> System.out.println("Item not found.")
        );
    }

    private static void tryMatches(Scanner sc, LostAndFoundService svc) {
        System.out.print("Enter item ID to find matches for: ");
        String id = sc.nextLine().trim();
        var opt = svc.findById(id);
        if (!opt.isPresent()) {
            System.out.println("Item not found.");
            return;
        }
        Item item = opt.get();
        var matches = svc.findMatchesFor(item);
        if (matches.isEmpty()) {
            System.out.println("No potential matches found.");
            return;
        }
        System.out.println("Potential matches (sorted by score):");
        for (var mr : matches) {
            System.out.printf("Score: %d - %s\n", mr.getScore(), mr.getItem());
        }
        System.out.println("If you see a correct match, note the matched item's ID and use option 6 to mark resolved.");
    }

    private static void resolveItem(Scanner sc, LostAndFoundService svc) {
        System.out.print("Enter item ID to mark as resolved: ");
        String id = sc.nextLine().trim();
        var opt = svc.findById(id);
        if (!opt.isPresent()) {
            System.out.println("Item not found.");
            return;
        }
        svc.markResolved(id);
        System.out.println("Item marked as resolved.");
    }
}