package data.repo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.model.Task;

public class JsonTaskRepository implements data.contract.TaskRepository {

    private final Path filePath;
    private List<Task> tasks;
    private long currentIdContext;

    public JsonTaskRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        this.tasks = new ArrayList<>();
        loadFromFile();
        this.currentIdContext = tasks.stream().mapToLong(Task::getId).max().orElse(0) + 1;
    }

    public JsonTaskRepository() {
        this.filePath = Paths.get("tasks.json");
        this.tasks = new ArrayList<>();
        loadFromFile();
        this.currentIdContext = tasks.stream().mapToLong(Task::getId).max().orElse(0) + 1;
    }

    @Override
    public Task add(String description) {
        Task newTask = Task.createNew(currentIdContext++, description);
        tasks.add(newTask);
        saveToFile();
        return newTask;
    }

    @Override
    public Task update(long id, String description) {
        Task existingTask = get(id).orElseThrow(() -> new RuntimeException("Task not found"));
        Task updatedTask = Task.restoreFromStorage(
            existingTask.getId(), 
            description, 
            existingTask.getBitfields(), 
            existingTask.getCreatedAt(), 
            Instant.now()
        );
        tasks.set(tasks.indexOf(existingTask), updatedTask);
        saveToFile();
        return updatedTask;
    }

    @Override
    public void delete(long id) {
        tasks.removeIf(t -> t.getId() == id);
        saveToFile();
    }

    @Override
    public Optional<Task> get(long id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst();
    }

    @Override
    public Optional<Task[]> getAll() {
        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks.toArray(new Task[0]));
    }

    @Override
    public Optional<Task[]> getAllDone() {
        return getByBitfield((byte) 2);
    }

    @Override
    public Optional<Task[]> getAllNotDone() {
        return getByBitfield((byte) 0);
    }

    @Override
    public Optional<Task[]> getAllInProgress() {
        return getByBitfield((byte) 1);
    }

    @Override
    public Task markDone() {
        throw new UnsupportedOperationException("Missing ID parameter in interface");
    }

    @Override
    public Task markInProgress() {
        throw new UnsupportedOperationException("Missing ID parameter in interface");
    }

    public Task markDone(long id) {
        return changeBitfield(id, (byte) 2);
    }

    public Task markInProgress(long id) {
        return changeBitfield(id, (byte) 1);
    }

    private Optional<Task[]> getByBitfield(byte bitfield) {
        Task[] filtered = tasks.stream()
                .filter(t -> t.getBitfields() == bitfield)
                .toArray(Task[]::new);
        return filtered.length > 0 ? Optional.of(filtered) : Optional.empty();
    }

    private Task changeBitfield(long id, byte newBitfield) {
        Task existingTask = get(id).orElseThrow(() -> new RuntimeException("Task not found"));
        Task updatedTask = Task.restoreFromStorage(
            existingTask.getId(), 
            new String(existingTask.getDescription()), 
            newBitfield, 
            existingTask.getCreatedAt(), 
            Instant.now()
        );
        tasks.set(tasks.indexOf(existingTask), updatedTask);
        saveToFile();
        return updatedTask;
    }

    private void saveToFile() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            json.append(String.format("  { \"id\": %d, \"description\": \"%s\", \"bitfields\": %d, \"createdAt\": \"%s\", \"updatedAt\": \"%s\" }",
                    t.getId(),
                    escapeJson(new String(t.getDescription())),
                    t.getBitfields(),
                    t.getCreatedAt().toString(),
                    t.getUpdatedAt().toString()));
            
            if (i < tasks.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");

        try {
            Files.writeString(filePath, json.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error saving JSON", e);
        }
    }

    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            return;
        }

        try {
            String content = Files.readString(filePath);
            Pattern objectPattern = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
            Matcher objectMatcher = objectPattern.matcher(content);

            while (objectMatcher.find()) {
                String rawJsonBlock = objectMatcher.group(1);
                Map<String, String> jsonMap = parseJsonBlockToMap(rawJsonBlock);

                long id = Long.parseLong(jsonMap.getOrDefault("id", "0"));
                String description = jsonMap.getOrDefault("description", "");
                byte bitfields = Byte.parseByte(jsonMap.getOrDefault("bitfields", "0"));
                
                Instant createdAt = jsonMap.containsKey("createdAt") 
                    ? Instant.parse(jsonMap.get("createdAt")) 
                    : Instant.now();
                    
                Instant updatedAt = jsonMap.containsKey("updatedAt") 
                    ? Instant.parse(jsonMap.get("updatedAt")) 
                    : Instant.now();

                tasks.add(Task.restoreFromStorage(id, description, bitfields, createdAt, updatedAt));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading JSON", e);
        }
    }

    private Map<String, String> parseJsonBlockToMap(String jsonBlock) {
        Map<String, String> map = new HashMap<>();
        Pattern keyValuePattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(?:\"([^\"]*)\"|([^\",}\\s]+))");
        Matcher matcher = keyValuePattern.matcher(jsonBlock);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            map.put(key, value);
        }
        return map;
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"");
    }
}