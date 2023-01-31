import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;


public class Duke {
    public static ArrayList<Task> todos = new ArrayList<>();

    public static class Task {
        protected String description;
        protected boolean isDone;

        public Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        public String getStatusIcon() {
            return (isDone ? "X" : " "); // mark done task with X
        }

        public void markAsDone() {
            this.isDone = true;
        }

        public void markAsNotDone() {
            this.isDone = false;
        }

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + this.description;
        }
    }

    public static class Todo extends Task {
        public Todo(String description) {
            super(description);
        }
        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    public static class Deadline extends Task {

        protected String by;

        public Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }
    }

    public static class Event extends Task {

        protected String from;
        protected String to;

        public Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + from + " to " + to + ")";
        }
    }

    public static void print(String s) {
        System.out.println(s);
    }

    public static class DukeCommandNotFoundException extends Exception {
        public DukeCommandNotFoundException (String msg) {
            super(msg);
        }
    }

    public static class DukeEmptyTaskException extends Exception {
        public DukeEmptyTaskException (String msg) {
            super(msg);
        }
    }

    public static String parse_date(String s) {
        DateTimeFormatter read_fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
        DateTimeFormatter print_fmt = DateTimeFormatter.ofPattern("MMM dd yyyy");
        try {
            LocalDate lt = LocalDate.parse(s, read_fmt);
            return lt.format(print_fmt);
        } catch (DateTimeParseException e) {
            print(e.toString());
        }
        return s;
    }

    public static void process_input(String input) throws DukeCommandNotFoundException, DukeEmptyTaskException {
        String trigger = input.split(" ")[0];
        int tid = 1;
        Task task;
        String content = "", ddl = "", from = "", to = "";
        switch (trigger) {
            case "bye":
                print("Bye. Hope to see you again soon!");
                System.exit(0);
            case "list":
                if (todos.isEmpty()) {
                    print("No items yet.");
                } else {
                    int i = 1;
                    for (Task t : todos) {
                        print(i + "." + t.toString());
                        i++;
                    }
                }
                break;
            case "mark":
                try {
                    tid = Integer.parseInt(input.split(" ")[1]);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    print(e.toString());
                    System.exit(1);
                }
                task = todos.get(tid - 1);
                task.markAsDone();
                print("Nice! I've marked this task as done:");
                print("\t" + task);
                break;
            case "unmark":
                try {
                    tid = Integer.parseInt(input.split(" ")[1]);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    print(e.toString());
                    System.exit(1);
                }
                task = todos.get(tid - 1);
                task.markAsNotDone();
                print("OK, I've marked this task as not done yet:");
                print("\t" + task);
                break;
            case "deadline":
                try {
                    input = input.split(trigger)[1];
                    content = input.split("/by")[0].strip();
                    ddl = input.split("/by")[1].strip();
                    ddl = parse_date(ddl);
                } catch (IndexOutOfBoundsException e) {
                    print(e.toString());
                    System.exit(1);
                }
                task = new Deadline(content, ddl);
                todos.add(task);
                print("Got it. I've added this task:");
                print("\t" + task);
                print("Now you have " + todos.size() + " tasks in the list.");
                break;
            case "event":
                if (input.split(trigger).length == 1) {
                    throw new DukeEmptyTaskException("☹ OOPS!!! The description of a " + trigger + " cannot be empty.");
                } else {
                    input = input.split(trigger)[1].strip();
                }
                try {
                    content = input.split("/from")[0].strip();
                    from = input.split("/from")[1].split("/to")[0].strip();
                    to = input.split("/from")[1].split("/to")[1].strip();
                    from = parse_date(from);
                    to = parse_date(to);
                } catch (IndexOutOfBoundsException e) {
                    print(e.toString());
                    System.exit(1);
                }
                task = new Event(content, from, to);
                todos.add(task);
                print("Got it. I've added this task:");
                print("\t" + task);
                print("Now you have " + todos.size() + " tasks in the list.");
                break;
            case "todo":
                if (input.split(trigger).length == 0) {
                    throw new DukeEmptyTaskException("☹ OOPS!!! The description of a " + trigger + " cannot be empty.");
                } else {
                    input = input.split(trigger)[1].strip();
                }
                task = new Todo(input);
                todos.add(task);
                print("Got it. I've added this task:");
                print("\t" + task);
                print("Now you have " + todos.size() + " tasks in the list.");
                break;
            case "delete":
                if (input.split(trigger).length == 1) {
                    throw new DukeEmptyTaskException("☹ OOPS!!! The description of a " + trigger + " cannot be empty.");
                }
                try {
                    tid = Integer.parseInt(input.split(trigger)[1].strip());
                    task = todos.get(tid - 1);
                    todos.remove(task);
                    print("Noted. I've removed this task:");
                    print("\t" + task);
                    print("Now you have " + todos.size() + " tasks in the list.");
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    print(e.toString());
                    System.exit(1);
                }
                break;
            default:
                throw new DukeCommandNotFoundException("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    public static void main(String[] args) {
        String greeting = "Hello! I'm Duke\n" +
                "     What can I do for you?";
        print(greeting);
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            try {
                process_input(input);
            } catch (Exception e) {
                print(e.toString());
            }
        }
    }
}