package data.contract;

//TODO: change to TaskModel add(), update(), get(), getAllXxx()
    
public interface TaskRepository {

    void add();
    void update();

    void delete();
    
    void get();
    void getAll();

    void getAllDone();
    void getAllNotDone();

    void getAllInProgress();

    void markDone();
    void markInProgress();

}