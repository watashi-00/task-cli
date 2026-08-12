# Task Tracker (roadmap.sh Challenge)

This is a solution to the [Task Tracker](https://roadmap.sh/projects/task-tracker) challenge from [roadmap.sh](https://roadmap.sh). The challenge description and requirements can be found in the [task.md](file:///home/watashi/Projects/roadmap.sh/TaskTracker/task-cli/task.md) file.

I have chosen **Java** to implement this project, adhering to the constraint of not using any external libraries or frameworks.

## Features

- **CLI Mode**: Run single commands directly from the terminal (e.g. `java -cp bin Application list`).
- **Interactive Mode**: Run a REPL session (e.g. `java -cp bin Application`) to issue multiple commands without restarting.
- **JSON Storage**: Tasks are saved to a local JSON file in the project directory using native file operations.

## Requirements

- **Java Development Kit (JDK)**: Version 8 or higher.

## Getting Started

### Compilation

First, compile the Java source files. The entry point is defined in [Application.java](file:///home/watashi/Projects/roadmap.sh/TaskTracker/task-cli/src/Application.java). Run the following command from the project root directory:

```bash
javac -d bin -sourcepath src src/Application.java
```

This compiles the application and outputs the `.class` files into the `bin` directory.

### Running the Application

You can execute the application in one of two modes:

#### 1. CLI Mode (Direct Command Execution)

Pass the command name and arguments directly after the class name. The app executes the command and exits immediately.

**Common Commands:**

```bash
# Add a new task
java -cp bin Application add "Buy groceries"

# Update a task's description
java -cp bin Application update 1 "Buy groceries and cook dinner"

# Mark a task's status
java -cp bin Application mark-in-progress 1
java -cp bin Application mark-done 1

# List tasks
java -cp bin Application list
java -cp bin Application list todo
java -cp bin Application list in-progress
java -cp bin Application list done

# Delete a task
java -cp bin Application delete 1
```

#### 2. Interactive / Application Mode (REPL)

Run the program with no arguments to start the interactive terminal session. This allows running multiple commands in succession.

```bash
java -cp bin Application
```

Once started, you will see a prompt where you can type commands directly (without `java -cp bin Application`):

```text
Task Tracker CLI Started! Type 'help' for commands or 'exit' to quit.
task-cli> add "Buy groceries"
Task added successfully (ID: 1)
task-cli> list
Task{id=1, description='Buy groceries', status=todo, createdAt=..., updatedAt=...}
task-cli> mark-in-progress 1
Task marked as in-progress (ID: 1)
task-cli> list in-progress
Task{id=1, description='Buy groceries', status=in-progress, createdAt=..., updatedAt=...}
task-cli> help
task-cli> exit
```
