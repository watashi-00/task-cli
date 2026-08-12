package repository.contract;

//TODO: change to TaskModel add(), update(), get(), getAllXxx()
    
public interface ImplTaskRepository {

    void add();
    void update();

    void delete();
    
    void get();
    void getAll();

    void getAllDone();
    void getAllNotDone();

    void getAllInProgress();

}