package data.model;

import java.time.Instant;

public class Task {
    static long id;
    char[] description;
    byte bitfields;
    // 0000000 -> todo
    // 0000001 -> in-progress
    // 0000010 -> done
    // 0000100 -> last id fetched
    final Instant createdAt;
    Instant updatedAt;

    Task(String description) {
        id = 0L;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.description = description.toCharArray();
    }



}
