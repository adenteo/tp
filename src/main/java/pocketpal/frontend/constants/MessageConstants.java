package pocketpal.frontend.constants;

public final class MessageConstants {
    public static final String NEWLINE = System.lineSeparator();
    public static final String LOGO = " _____           _        _   _____      _" + NEWLINE
            + "|  __ \\         | |      | | |  __ \\    | |" + NEWLINE
            + "| |__) |__   ___| | _____| |_| |__) |_ _| |" + NEWLINE
            + "|  ___/ _ \\ / __| |/ / _ \\ __|  ___/ _` | |" + NEWLINE
            + "| |  | (_) | (__|   <  __/ |_| |  | (_| | |" + NEWLINE
            + "|_|   \\___/ \\___|_|\\_\\___|\\__|_|   \\__,_|_|";
    public static final String CATEGORY = "Category: ";
    public static final String DESCRIPTION = "Description: ";
    public static final String PRICE = "Price: $";

    // Command Messages
    public static final String MESSAGE_EXIT = "Bye. See you again :)" + NEWLINE;
    public static final String MESSAGE_EXPENDITURE_ADDED = "The following expenditure has been added:" + NEWLINE;
    public static final String MESSAGE_EXPENDITURE_DELETED = "The following expenditure has been deleted:" + NEWLINE;
    public static final String MESSAGE_EXPENDITURE_EDITED = "The following expenditure has been updated:" + NEWLINE;
    public static final String MESSAGE_NO_ENTRIES = "There are no entries available." + NEWLINE;
    public static final String MESSAGE_ADD_COMMAND = "Add - Adds an expense to your current expenditure." + NEWLINE
            + "Usage: /add <DESCRIPTION> <-c CATEGORY> <-p PRICE>" + NEWLINE
            + NEWLINE;
    public static final String MESSAGE_DELETE_COMMAND = "Delete - Deletes a specified expense from your expenditure."
            + NEWLINE
            + "Usage: /delete <EXPENSE_ID> [ADDITIONAL_EXPENSE_ID...]" + NEWLINE + NEWLINE;
    public static final String MESSAGE_EDIT_COMMAND = "Edit - Edits a specified expense in your current expenditure."
            + NEWLINE
            + "Usage: /edit <EXPENSE_ID> [FLAG...]" + NEWLINE + NEWLINE;
    public static final String MESSAGE_VIEW_COMMAND = "View - Displays a list of your current expenditure."
            + NEWLINE
            + "Usage: /view [COUNT] [-c | -category CATEGORY]" + NEWLINE + NEWLINE;
    public static final String MESSAGE_HELP_COMMAND = "Help - Displays the help menu." + NEWLINE
            + "Usage: /help" + NEWLINE + NEWLINE;
    public static final String MESSAGE_BYE_COMMAND = "Exit - Terminates PocketPal." + NEWLINE
            + "Usage: /bye" + NEWLINE;
    public static final String MESSAGE_WELCOME = "Welcome to" + NEWLINE
            + LOGO + NEWLINE + NEWLINE
            + "How may I help you?" + NEWLINE;
    public static final String MESSAGE_HELP = "PocketPal is a expense tracking app, "
            + "optimised for use via a Command Line Interface. " + NEWLINE
            + "Users can take advantage of the input flags for entering entries quickly." + NEWLINE
            + "Listed below are the various commands that are currently supported." + NEWLINE + NEWLINE;

    // Exception Messages
    public static final String MESSAGE_EMPTY_INPUT = "Use /help for a list of supported commands!";
    public static final String MESSAGE_INVALID_COMMAND = "Please specify a valid command!";
    public static final String MESSAGE_INVALID_CATEGORY = "Please specify a valid category!";
    public static final String MESSAGE_INVALID_DESCRIPTION = "Description cannot contain any commas (,)!";
    public static final String MESSAGE_INVALID_ID = "Please specify a valid numerical index!";
    public static final String MESSAGE_INVALID_PRICE = "Please specify a numeric or decimal value for the price!";
    public static final String MESSAGE_NON_POSITIVE_PRICE = "Price must be greater than zero!";
    public static final String MESSAGE_INVALID_PRICE_RANGE = "Please specify a valid price range!";

    public static final String MESSAGE_DUPLICATE_OPTION = "Please ensure that any option is not declared more than " +
            "once!";
    public static final String MESSAGE_UNKNOWN_OPTION = "Unknown option used: ";
    public static final String MESSAGE_MISSING_ARGUMENT = "Missing argument for option: ";
    public static final String MESSAGE_MISSING_ARGS_ADD = "Please specify the description, category and price!";
    public static final String MESSAGE_MISSING_PRICE_ADD = "Please specify the price using the '-p' option!";
    public static final String MESSAGE_MISSING_DESCRIPTION_ADD = "Please specify the description using the '-d' option!";
    public static final String MESSAGE_MISSING_CATEGORY_ADD = "Please specify the category using the '-c' option!";
    public static final String MESSAGE_MISSING_ARGS_EDIT = "Please specify the ID of the expense you would like to " +
            "edit!";
    public static final String MESSAGE_INVALID_DATE = "Please enter date in dd/MM/yy format" +
            " and make sure the date exists! eg. 12/01/99 or 12/12/00";
    public static final String MESSAGE_MIXED_DATE = "start date should be before end date!";
    public static final String MESSAGE_MISSING_DATE = "Please enter BOTH the start and end date!";
    public static final String MESSAGE_INVALID_NUMBER_OF_ENTRIES = "Please enter a positive integer more than zero!";
    public static final String MESSAGE_INVALID_DATE_READ = "Date format not recognised.";

    private MessageConstants() {
    }
}
