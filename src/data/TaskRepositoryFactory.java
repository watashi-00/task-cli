package data;

import data.contract.TaskRepository;
import data.repo.JsonTaskRepository;

public class TaskRepositoryFactory {

    public static TaskRepository getRepository(String storageType) {

        if (storageType == null || storageType.trim().isEmpty()) {
            throw new IllegalArgumentException("Storage type is blank or null");
        }

        switch (storageType) {
            case "json":
            case "file":
                return new JsonTaskRepository();
            default:
                throw new IllegalArgumentException("Storage type is not valid: " + storageType);
        }
    } 
}