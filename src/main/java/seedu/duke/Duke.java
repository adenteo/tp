package seedu.duke;

import java.util.Scanner;

import seedu.duke.commands.Command;
import seedu.duke.constants.UIConstants;
// import seedu.duke.entries.Entry;
import seedu.duke.entrylog.EntryLog;
import seedu.duke.parser.Parser;
import seedu.duke.ui.UI;

public class Duke {
    /**
     * Main entry-point for the java.duke.Duke application.
     */
    public static void main(String[] args) {
        UI ui = new UI();
        ui.printWelcome();
        EntryLog entrylog = new EntryLog();
        Scanner in = new Scanner(System.in);
        do {
            ui.printAwaitUserInput();
            String userInput = in.nextLine();
            ui.printLine();
            try {
                Command command = new Parser().parseUserInput(userInput);
                command.execute(entrylog);
            } catch (Exception e) {
                ui.print("error");
            }
            // TODO: condition to be replaced when exit command is implemented
        } while (in.hasNextLine());
    }
}
