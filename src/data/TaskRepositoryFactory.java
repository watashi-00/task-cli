package data;

import data.contract.TaskRepository;
import data.repo.JsonTaskRepository;

public class TaskRepositoryFactory {

    public static TaskRepository getRepository(String storageType) {

        if(storageType == null || storageType.isBlank()) {
            throw new IllegalArgumentException("Storage type is blank or null");
        }

        return switch (storageType) {
            case "json", "file" -> new JsonTaskRepository();
            default -> throw new IllegalArgumentException("Storage type is not valid");
        };

    } 

}