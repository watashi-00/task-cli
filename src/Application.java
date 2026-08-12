import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.TaskRepositoryFactory;
import data.contract.TaskRepository;
import data.model.Task;
import data.repo.JsonTaskRepository;

public class Application {
    private static boolean mode; // true = App | false = Cli
    private static Scanner scanner;
    
    private TaskRepository repository;

    public Application() {
        this.repository = TaskRepositoryFactory.getRepository("json");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            mode = true;
            scanner = new Scanner(System.in);
            new Application().run();
        } else {
            mode = false;
            new Application().exec(args);
        }
    }

    void run() {
        System.out.println("Task Tracker CLI Started! Type 'help' for commands or 'exit' to quit.");
        
        while (true) {
            System.out.print("task-cli> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Bye!");
                break;
            }

            if (input.isEmpty()) {
                continue;
            }

            String[] parsedArgs = parseCommandLineArguments(input);
            exec(parsedArgs);
        }
    }

    void exec(String[] args) {
        String op = args[0].toLowerCase();

        try {
            switch (op) {
                case "add":
                    if (args.length < 2) throw new IllegalArgumentException("Description is required.");
                    Task addedTask = repository.add(args[1]);
                    System.out.println("Task added successfully (ID: " + addedTask.getId() + ")");
                    break;

                case "update":
                    if (args.length < 3) throw new IllegalArgumentException("ID and description are required.");
                    long updateId = parseId(args[1]);
                    repository.update(updateId, args[2]);
                    System.out.println("Task updated successfully (ID: " + updateId + ")");
                    break;

                case "delete":
                    if (args.length < 2) throw new IllegalArgumentException("ID is required.");
                    long deleteId = parseId(args[1]);
                    repository.delete(deleteId);
                    System.out.println("Task deleted successfully (ID: " + deleteId + ")");
                    break;

                case "mark-in-progress":
                    if (args.length < 2) throw new IllegalArgumentException("ID is required.");
                    long mipId = parseId(args[1]);
                    ((JsonTaskRepository) repository).markInProgress(mipId); 
                    System.out.println("Task marked as in-progress (ID: " + mipId + ")");
                    break;

                case "mark-done":
                    if (args.length < 2) throw new IllegalArgumentException("ID is required.");
                    long mdId = parseId(args[1]);
                    ((JsonTaskRepository) repository).markDone(mdId);
                    System.out.println("Task marked as done (ID: " + mdId + ")");
                    break;

                case "list":
                    Optional<Task[]> tasksOpt;
                    if (args.length == 1) {
                        tasksOpt = repository.getAll();
                    } else {
                        String filter = args[1].toLowerCase();
                        switch (filter) {
                            case "done":
                                tasksOpt = repository.getAllDone();
                                break;
                            case "todo":
                                tasksOpt = repository.getAllNotDone();
                                break;
                            case "in-progress":
                                tasksOpt = repository.getAllInProgress();
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid status. Use: done, todo, or in-progress");
                        }
                    }

                    if (tasksOpt.isPresent() && tasksOpt.get().length > 0) {
                        for (Task t : tasksOpt.get()) {
                            System.out.println(t);
                        }
                    } else {
                        System.out.println("No tasks found.");
                    }
                    break;

                case "help":
                    System.out.println("Available commands:");
                    System.out.println("  add <description>");
                    System.out.println("  update <id> <description>");
                    System.out.println("  delete <id>");
                    System.out.println("  mark-in-progress <id>");
                    System.out.println("  mark-done <id>");
                    System.out.println("  list [done|todo|in-progress]");
                    break;

                default:
                    System.out.println("Unknown command: '" + op + "'. Type 'help' for available commands.");
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("System Error: " + e.getMessage());
        }
    }

    private long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot convert '" + id + "' to a valid Task ID", e);
        }
    }

    public static boolean isMode() {
        return mode;
    }

    private String[] parseCommandLineArguments(String line) {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(line);
        while (m.find()) {
            list.add(m.group(1).replace("\"", ""));
        }
        return list.toArray(new String[0]);
    }
}