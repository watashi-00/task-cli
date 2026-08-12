package data.contract;

import java.util.Optional;

import data.model.Task;
    
public interface TaskRepository {

    Task add(String description);
    Task update(long id, String description);

    void delete(long id);
    
    Optional<Task> get(long id);
    Optional<Task[]> getAll();

    Optional<Task[]> getAllDone();
    Optional<Task[]> getAllNotDone();

    Optional<Task[]> getAllInProgress();

    Task markDone();
    Task markInProgress();

}