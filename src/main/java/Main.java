import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    /**
     * Entry point of the program.
     * Greets the user, loads saved tasks, and starts the takeInput loop.
     */
    void main() {
        greet(); // calls showTasks() which in turn invokes loadTasks()
        takeInput(); // user input handler
    }

    // private constants
    private final List<String> TASK_DESCRIPTIONS = new ArrayList<>();
    private final List<Boolean> TASK_STATUS = new ArrayList<>();
    private final String FILE_NAME = "Todo.txt";

    /**
     * Displays a welcome message and shows existing tasks.
     */
    public void greet() {
        IO.println("Welcome to the Todo App");
        IO.println("-----------------------");
        showTasks();
    }

    /**
     * Prints the main menu options for the user.
     */
    public void printMenu() {
        IO.println("What would you like to do?");
        IO.println("a - add task");
        IO.println("c - complete task");
        IO.println("r - remove completed tasks");
        IO.println("q - quit");
    }

    /**
     * Handles user input and performs actions such as add, complete, remove, or quit without crashing.
     */
    public void takeInput() {
        Scanner input = new Scanner(System.in);
        boolean flag = true;

        while(flag) {
            printMenu();
            IO.print("> ");
            String userChoice = input.hasNextLine()? input.nextLine().trim().toLowerCase() : "q";
            switch(userChoice) {

                case "a": // add task
                    IO.println("Enter new task");
                    IO.print("> ");
                    String description = input.hasNextLine()? input.nextLine().trim().toLowerCase() : "";
                    if (description.isEmpty()) {
                        IO.println("Missing task description");
                    } else {
                        addTask(description);
                        saveTasks(); // saves to file
                        IO.println("New task added");
                    }
                    showTasks();
                    break;

                case "c": // Mark task as completed
                    if (TASK_DESCRIPTIONS.isEmpty()) {
                        IO.println("No tasks to mark as complete");
                        break;
                    }
                    IO.println("Enter task number to mark complete: ");
                    IO.print("> ");
                    String numStr = input.hasNextLine() ? input.nextLine().trim() : ""; // error prone
                    try {
                        int n = Integer.parseInt(numStr); // need to convert to integer
                        boolean ok = completeTask(n); // n is +1 of index, will be handled in handler function
                        if (ok) { // true
                            saveTasks(); // save to file
                            IO.println("Task marked complete!");
                        } else { // false
                            IO.println("Invalid task number.");
                        }
                    } catch (NumberFormatException e) {
                        IO.println("Please enter a number like 1, 2, 3...");
                    }
                    showTasks();
                    break;

                case "r": // remove completed task from file
                    removeCompleted();
                    saveTasks();
                    IO.println("Completed tasks removed!");
                    showTasks();
                    break;
                case "q": // quit the app
                    {
                    IO.println("Good bye!");
                    flag = false; // exit
                    break;
                }
                default: {
                    IO.println("Please enter a valid option!");
                    break;
                }
            }
        }
    }

    /**
     * Displays all current tasks with their completion status.
     */
    public void showTasks() {
        IO.println("Your tasks:");
        loadTasks(); // loads tasks persistently from text file
        if (!TASK_DESCRIPTIONS.isEmpty()) {
            for (int i = 0; i < TASK_DESCRIPTIONS.size(); i++) {
                String completionMark = TASK_STATUS.get(i) ? "✓" : " "; // sets mark based on the returned boolean value by taskCompleted.get(i)
                IO.println((i + 1) + ". [" + completionMark + "] " + TASK_DESCRIPTIONS.get(i));
            }
        }
        else
            IO.println("Empty tasks list");
    }

    /**
     * Adds a new task to the list (initially, incomplete)
     * @param description:String the task description entered by the user
     */
    public void addTask(String description) {
        TASK_DESCRIPTIONS.add(description);
        TASK_STATUS.add(false); // initially
    }

    /**
     * Marks a specific task as completed.
     * @param taskNumber:int the number of the task (+1)
     * @return true if successful, false if invalid number
     */
    public boolean completeTask(int taskNumber) {
        int index = taskNumber - 1; // since, taskNumber entered by user will be +1 we need to -1 to match the index perfectly
        if (index < 0 || index >= TASK_DESCRIPTIONS.size()) {
            return false;
        }
        TASK_STATUS.set(index, true); // set the task at that index to true i.e completed
        return true;
    }

    /**
     * Removes all completed tasks from the list.
     */
    public void removeCompleted() {
        for (int i = TASK_DESCRIPTIONS.size() - 1; i >= 0; i--) {
            if (TASK_STATUS.get(i)) {
                TASK_DESCRIPTIONS.remove(i);
                TASK_STATUS.remove(i);
            }
        }
    }

    /**
     * Saves all current tasks to a text file.
     * Each line starts with task number followed by the description.
     * Format -> 0/1|task-name, 0 = incomplete task and 1 = completed task
     */
    public void saveTasks() {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            for (int i = 0; i < TASK_DESCRIPTIONS.size(); i++) {
                String flag = TASK_STATUS.get(i) ? "1" : "0"; // if completed task name precedes with 1, else 0
                writer.println(flag + "|" + TASK_DESCRIPTIONS.get(i));
            }
        } catch (Exception e) {
            IO.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Loads saved tasks from the text file.
     * The text file uses "|" to separate completion flag and description.
     * Completion flag = 1 = Task completed
     * Completion flag = 0 = Task incomplete
     */
    public void loadTasks() {
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            return; // nothing to load
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            TASK_DESCRIPTIONS.clear();
            TASK_STATUS.clear();
            while ((line = br.readLine()) != null)
            // while there's more to read...
            {
                String[] parts = line.split("\\|", 2); // split strings at '|' with limit of 2
                if (parts.length == 2) {
                    boolean done = parts[0].trim().equals("1"); // if 1, mark as complete and add to taskCompleted<>
                    String desc = parts[1].trim(); // trim the description and add to taskDescriptions<>
                    TASK_DESCRIPTIONS.add(desc);
                    TASK_STATUS.add(done);
                }
            }
        } catch (IOException e) {
            IO.println("Could not load tasks: " + e.getMessage());
        }
    }
}
