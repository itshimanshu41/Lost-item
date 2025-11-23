import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LostAndFoundService {
    private final Repository repo;
    private List<Item> cache;

    public LostAndFoundService(Repository repo) {
        this.repo = repo;
        this.cache = repo.loadAll();
    }

    public synchronized Item reportLost(String title, String type, String desc, String location,
                                        LocalDate date, String reporter, String contact) {
        Item item = new Item(title, type, desc, location, date, reporter, contact, false);
        repo.saveItem(item);
        cache.add(item);
        return item;
    }

    public synchronized Item reportFound(String title, String type, String desc, String location,
                                         LocalDate date, String reporter, String contact) {
        Item item = new Item(title, type, desc, location, date, reporter, contact, true);
        repo.saveItem(item);
        cache.add(item);
        return item;
    }

    public synchronized List<Item> listAll() {
        return new ArrayList<>(cache);
    }

    public synchronized Optional<Item> findById(String id) {
        return cache.stream().filter(i -> i.getId().equals(id)).findFirst();
    }

    public synchronized void markResolved(String id) {
        Optional<Item> opt = findById(id);
        if (opt.isPresent()) {
            opt.get().setResolved(true);
            repo.saveAll(cache);
        }
    }

    // simple matching: type must equal (case-insensitive) and location contains same tokens and
    // description overlapping words increases score.
    public synchronized List<MatchResult> findMatchesFor(Item query) {
        List<MatchResult> results = new ArrayList<>();
        for (Item other : cache) {
            // only match opposite found/lost and not resolved
            if (other.isFound() == query.isFound()) continue;
            if (other.isResolved() || query.isResolved()) continue;

            int score = 0;
            if (safeEqualsIgnoreCase(query.getType(), other.getType())) score += 5;
            if (tokenOverlap(query.getLocation(), other.getLocation()) > 0) score += 3;
            score += tokenOverlap(query.getDescription(), other.getDescription());

            // Recent items get small boost
            long daysDiff = Math.abs(query.getDate().toEpochDay() - other.getDate().toEpochDay());
            if (daysDiff <= 3) score += 2;

            if (score > 0) results.add(new MatchResult(other, score));
        }
        results.sort(Comparator.comparingInt(MatchResult::getScore).reversed());
        return results;
    }

    private static boolean safeEqualsIgnoreCase(String a, String b) {
        if (a == null) return b == null;
        return a.trim().equalsIgnoreCase(b == null ? "" : b.trim());
    }

    private static int tokenOverlap(String a, String b) {
        if (a == null || b == null) return 0;
        Set<String> sa = Arrays.stream(a.toLowerCase().split("\\W+"))
                .filter(s -> s.length() > 0).collect(Collectors.toSet());
        Set<String> sb = Arrays.stream(b.toLowerCase().split("\\W+"))
                .filter(s -> s.length() > 0).collect(Collectors.toSet());
        sa.retainAll(sb);
        return sa.size();
    }

    public static class MatchResult {
        private final Item item;
        private final int score;
        public MatchResult(Item item, int score) { this.item = item; this.score = score; }
        public Item getItem() { return item; }
        public int getScore() { return score; }
    }
}