package data.model;

import java.time.Instant;

public class Task {
    
    private long id;
    private char[] description;
    private byte bitfields;
    // 0000000 -> todo
    // 0000001 -> in-progress
    // 0000010 -> done
    private final Instant createdAt;
    private Instant updatedAt;

    private Task(long id, String description, byte bitfields, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.description = description.toCharArray();
        this.bitfields = bitfields;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Task createNew(long generatedId, String description) {
        Instant now = Instant.now();
        return new Task(generatedId, description, (byte) 0, now, now);
    }

    public static Task restoreFromStorage(long id, String description, byte bitfields, Instant createdAt, Instant updatedAt) {
        return new Task(id, description, bitfields, createdAt, updatedAt);
    }

    public long getId() {
        return id;
    }

    public char[] getDescription() {
        return description;
    }

    public byte getBitfields() {
        return bitfields;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + String.valueOf(description) + '\'' +
                ", bitfields=" + bitfields +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}