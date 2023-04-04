// @@author adenteo
package pocketpal.frontend.parser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pocketpal.data.entry.Category;
import pocketpal.frontend.constants.EntryConstants;
import pocketpal.frontend.constants.MessageConstants;
import pocketpal.frontend.commands.Command;
import pocketpal.frontend.commands.AddCommand;
import pocketpal.frontend.commands.DeleteCommand;
import pocketpal.frontend.commands.EditCommand;
import pocketpal.frontend.commands.ExitCommand;
import pocketpal.frontend.commands.HelpCommand;
import pocketpal.frontend.commands.ViewCommand;
import pocketpal.frontend.exceptions.*;
import pocketpal.frontend.util.CategoryUtil;
import pocketpal.frontend.util.StringUtil;

public class Parser {

    private static final String COMMAND_ADD = "/add";
    private static final String COMMAND_VIEW = "/view";
    private static final String COMMAND_EDIT = "/edit";
    private static final String COMMAND_DELETE = "/delete";
    private static final String COMMAND_HELP = "/help";
    private static final String COMMAND_BYE = "/bye";
    private static final String PRICE_FLAG = "-p ";
    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    private static final String VALID_PRICE_REGEX = "[0-9.-]*";
    private static final String categoryOption = "-c|-category";
    private static final String priceOption = "-p|-price";
    private static final String descriptionOption = "-d|-description";
    private static final String startdateOption = "-sd|-startdate";
    private static final String enddateOption = "-ed|-enddate";

    private static final String startpriceOption = "-sp|-startprice";
    private static final String endpriceOption = "-ep|-endprice";
    private static final String availableOptions =
            categoryOption + "|" + priceOption + "|" + descriptionOption + "|" + startdateOption + "|" + enddateOption + "|" + startpriceOption + "|" + endpriceOption;
    private static final String availableOptionsAddEdit = categoryOption + "|" + priceOption + "|" + descriptionOption;
    private static final String availableOptionsView =
            categoryOption + "|" + startpriceOption + "|" + endpriceOption + "|" + startdateOption + "|" + enddateOption;

    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("(-d|-description)(\\s+(.*?))?" +
            "(\\s+$" + availableOptions + ")");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("(-c|-category)(\\s+\\S+)?");
    private static final Pattern PRICE_PATTERN = Pattern.compile("(-p|-price)(\\s+\\S+)?");
    private static final Pattern START_PRICE_PATTERN = Pattern.compile("(-sp|-startprice)(\\s+\\S+)?");
    private static final Pattern END_PRICE_PATTERN = Pattern.compile("(-ep|-endprice)(\\s+(\\S+))?");
    private static final Pattern ID_PATTERN = Pattern.compile("(\\s+)?(\\S+)");
    private static final Pattern START_DATE_PATTERN = Pattern.compile("(-sd|-startdate)\\s+(0*\\d+/0*\\d+/\\d{2,})");
    private static final Pattern END_DATE_PATTERN = Pattern.compile("(-ed|-enddate)\\s+(0*\\d+/0*\\d+/\\d{2,})");

    /**
     * Returns a Command object that is to be executed by the backend. If any input
     * requirements are not met, the respective exceptions will be thrown and error
     * messages will be displayed to the user
     * via the UI.
     *
     * @param userInput Entire user input string
     * @return Command Command object to be executed
     * @throws InvalidCommandException   If command entered is invalid
     * @throws InvalidArgumentsException If arguments entered are in wrong format
     * @throws MissingArgumentsException If required arguments are missing
     * @throws InvalidCategoryException  If category entered is invalid
     * @throws MissingDateException      If required date is missing
     * @throws InvalidDateException      If date entered is invalid
     */
    public Command parseUserInput(String userInput) throws
            InvalidCommandException, InvalidArgumentsException,
            MissingArgumentsException, InvalidCategoryException, MissingDateException, InvalidDateException, DuplicateOptionException, UnknownOptionException {
        logger.entering(Parser.class.getName(), "parseUserInput()");
        userInput = userInput.trim();
        if (userInput.isEmpty()) {
            logger.log(Level.WARNING, "User entered an empty string",
                    new MissingArgumentsException(MessageConstants.MESSAGE_EMPTY_INPUT));
            throw new MissingArgumentsException(MessageConstants.MESSAGE_EMPTY_INPUT);
        }
        String[] userInputArray = userInput.split(" ", 2);
        assert userInputArray.length >= 1 : "Input must contain at least one command";
        String command = userInputArray[0].toLowerCase();
        String arguments = "";
        if (userInputArray.length > 1) {
            arguments = userInputArray[1];
            checkUnknownOption(arguments, availableOptions);
        }
        logger.log(Level.INFO, "User input command: " + command);
        logger.log(Level.INFO, "User input arguments: " + arguments);
        switch (command) {
        case COMMAND_ADD:
            logger.exiting(Parser.class.getName(), "parseUserInput()");
            return parseAddCommand(arguments);
        case COMMAND_VIEW:
            logger.exiting(Parser.class.getName(), "parseUserInput()");
            return parseViewCommand(arguments);
        case COMMAND_EDIT:
            logger.exiting(Parser.class.getName(), "parseUserInput()");
            return parseEditCommand(arguments);
        case COMMAND_DELETE:
            logger.exiting(Parser.class.getName(), "parseUserInput()");
            return parseDeleteCommand(arguments);
        case COMMAND_HELP:
            logger.exiting(Parser.class.getName(), "parseUserInput()");
            return parseHelpCommand();
        case COMMAND_BYE:
            logger.exiting(Parser.class.getName(), "parseUserInput()");
            return parseByeCommand();
        default:
            logger.log(Level.WARNING, "User command is invalid");
            logger.exiting(Parser.class.getName(), "parseUserInput()");
            throw new InvalidCommandException(MessageConstants.MESSAGE_INVALID_COMMAND);
        }
    }

    /**
     * Returns a string array of length 3, containing the description, category and
     * price respectively. If any of the fields are missing, an empty string is
     * returned for that field and an error will be raised
     *
     * @param arguments User arguments entered after the add command.
     * @return String[] Array containing description, category and price
     * respectively.
     */
    private String[] parseAddArguments(String arguments) throws MissingArgumentsException {
        logger.entering(Parser.class.getName(), "parseAddArguments()");
        String description;
        String category;
        String price;
        String[] argumentsArray = new String[3];
        description = extractDetail(arguments, DESCRIPTION_PATTERN);
        category = extractDetail(arguments, CATEGORY_PATTERN);
        price = extractDetail(arguments, PRICE_PATTERN);
        if (description == null) {
            logger.warning("Missing description: " + MessageConstants.MESSAGE_MISSING_DESCRIPTION_ADD);
            throw new MissingArgumentsException(MessageConstants.MESSAGE_MISSING_DESCRIPTION_ADD);
        }
        if (category == null) {
            logger.warning("Missing category: " + MessageConstants.MESSAGE_MISSING_CATEGORY_ADD);
            throw new MissingArgumentsException(MessageConstants.MESSAGE_MISSING_CATEGORY_ADD);
        }
        if (price == null) {
            logger.warning("Missing price: " + MessageConstants.MESSAGE_MISSING_PRICE_ADD);
            throw new MissingArgumentsException(MessageConstants.MESSAGE_MISSING_PRICE_ADD);
        }
        argumentsArray[0] = description;
        argumentsArray[1] = category;
        argumentsArray[2] = price;
        logger.exiting(Parser.class.getName(), "parseAddArguments()");
        return argumentsArray;
    }


    /**
     * Returns an AddCommand object to be executed by the backend. The AddCommand
     * contains the parameters of the expenses to be added to the expense list.
     *
     * @param arguments User input entered after add command.
     * @return Command AddCommand object to be executed.
     * @throws MissingArgumentsException If required arguments are missing.
     * @throws InvalidArgumentsException If required arguments are in wrong format.
     * @throws InvalidCategoryException  If category entered is invalid.
     */
    private Command parseAddCommand(String arguments)
            throws MissingArgumentsException, InvalidArgumentsException, InvalidCategoryException, DuplicateOptionException, UnknownOptionException {
        logger.entering(Parser.class.getName(), "parseAddCommand()");
        logger.info("Parsing add command with arguments: " + arguments);
        if (arguments.isEmpty()) {
            throw new MissingArgumentsException(MessageConstants.MESSAGE_MISSING_ARGS_ADD);
        }
        checkUnknownOption(arguments, availableOptionsAddEdit);
        checkDuplicateOptionsAddEdit(arguments);
        String[] argumentsArray = parseAddArguments(arguments);
        assert argumentsArray.length == 3 : "User input must contain description, category, and price";
        String description = argumentsArray[0];
        checkIfDescriptionValid(description);
        String category = argumentsArray[1];
        String price = argumentsArray[2];
        logger.info("User input description: " + description);
        logger.info("User input category: " + category);
        logger.info("User input price: " + price);
        double priceDouble;
        checkIfPriceValid(price);
        priceDouble = Double.parseDouble(price);
        if (priceDouble <= 0) {
            throw new InvalidArgumentsException(MessageConstants.MESSAGE_NON_POSITIVE_PRICE);
        }
        logger.exiting(Parser.class.getName(), "parseAddCommand()");
        return new AddCommand(description, priceDouble, category);
    }


    /**
     * Returns an ExitCommand object to be executed by the backend. The ExitCommand
     * will terminate the program.
     *
     * @return Command ExitCommand object to be executed.
     */
    private Command parseByeCommand() {
        // Print bye message
        logger.entering(Parser.class.getName(), "parseByeCommand()");
        logger.info("Program exiting.");
        logger.exiting(Parser.class.getName(), "parseByeCommand()");
        return new ExitCommand();
    }


    /**
     * Returns an DeleteCommand object to be executed by the backend. The
     * DeleteCommand takes in an integer index of the expense to be deleted.
     *
     * @param arguments User input after the delete command.
     * @return Command DeleteCommand object to be executed.
     * @throws InvalidArgumentsException If entered expense ID does not exist.
     * @throws MissingArgumentsException If required expense ID is not entered.
     */
    private Command parseDeleteCommand(String arguments)
            throws InvalidArgumentsException, MissingArgumentsException {
        logger.entering(Parser.class.getName(), "parseDeleteCommand()");
        logger.info("Parsing delete command with arguments: " + arguments);
        if (arguments.isEmpty()) {
            logger.log(Level.WARNING, "Index of the expense not specified");
            throw new MissingArgumentsException(MessageConstants.MESSAGE_INVALID_ID);
        }
        String[] argumentsArray = arguments.split(" ");
        assert argumentsArray.length >= 1 : "User input must contain at least one argument";
        int expenseIdInt;
        Integer[] expenseIds = new Integer[argumentsArray.length];
        try {
            for (int i = 0; i < argumentsArray.length; i++) {
                expenseIdInt = Integer.parseInt(argumentsArray[i]);
                if (expenseIdInt < 1) {
                    throw new InvalidArgumentsException(MessageConstants.MESSAGE_INVALID_ID);
                }
                assert argumentsArray[i].matches("\\d+") : "Expense ID must be an integer";
                logger.log(Level.INFO, "Removing specified expense id {0} from list", argumentsArray[i]);
                expenseIds[i] = expenseIdInt;
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Index of the expense specified is not an integer");
            throw new InvalidArgumentsException(MessageConstants.MESSAGE_INVALID_ID);
        }
        logger.exiting(Parser.class.getName(), "parseDeleteCommand(arguments)");
        return new DeleteCommand(expenseIds);
    }


    /**
     * Returns a HelpCommand object to be executed by the backend. The help menu is
     * displayed to the user when this command is executed
     *
     * @return Command HelpCommand to be executed.
     */
    private Command parseHelpCommand() {
        // Print help message
        logger.entering(Parser.class.getName(), "parseHelpCommand()");
        logger.info("Displaying help message.");
        logger.exiting(Parser.class.getName(), "parseHelpCommand()");
        return new HelpCommand();
    }


    /**
     * Returns an array containing the expenseId, description, category and price of
     * the expense respectively. If any of the fields are empty, an empty string is returned.
     *
     * @param arguments User input after the edit command.
     * @return String[] Array containing new expenseId, description, category and price
     */
    private String[] parseEditArguments(String arguments) throws MissingArgumentsException {
        logger.entering(Parser.class.getName(), "parseEditArguments()");
        String expenseId;
        String description;
        String category;
        String price;
        String[] argumentsArray = new String[4];
        expenseId = extractDetail(arguments, ID_PATTERN);
        description = extractDetail(arguments, DESCRIPTION_PATTERN);
        category = extractDetail(arguments, CATEGORY_PATTERN);
        price = extractDetail(arguments, PRICE_PATTERN);
        argumentsArray[0] = expenseId;
        argumentsArray[1] = description;
        argumentsArray[2] = category;
        argumentsArray[3] = price;
        logger.exiting(Parser.class.getName(), "parseEditArguments()");
        return argumentsArray;
    }


    /**
     * Returns an EditCommand object to be executed by the backend. The object
     * contains the new data to be updated for the specified expense. If the new
     * data is in incorrect format, error is raised to the user.
     *
     * @param arguments User input after edit command.
     * @return Command EditCommand object containing the new parameters to be
     * updated.
     * @throws MissingArgumentsException If required arguments are missing.
     * @throws InvalidArgumentsException If entered arguments are in incorrect format.
     */
    private Command parseEditCommand(String arguments) throws MissingArgumentsException, InvalidArgumentsException, DuplicateOptionException, UnknownOptionException {
        logger.entering(Parser.class.getName(), "parseEditCommand()");
        logger.info("Parsing arguments for edit command: " + arguments);
        if (arguments.isEmpty()) {
            logger.warning("Missing arguments for edit command: " + MessageConstants.MESSAGE_MISSING_ARGS_EDIT);
            throw new MissingArgumentsException(MessageConstants.MESSAGE_MISSING_ARGS_EDIT);
        }
        checkUnknownOption(arguments, availableOptionsAddEdit);
        checkDuplicateOptionsAddEdit(arguments);
        String[] argumentsArray = parseEditArguments(arguments);
        String expenseId = argumentsArray[0];
        String description = argumentsArray[1];
        String category = argumentsArray[2];
        String price = argumentsArray[3];
        logger.info("User input expense ID: " + expenseId);
        logger.info("User input description: " + description);
        logger.info("User input category: " + category);
        logger.info("User input price: " + price);
        try {
            Integer.parseInt(expenseId);
        } catch (NumberFormatException e) {
            logger.warning("Expense ID is not an integer: " + MessageConstants.MESSAGE_INVALID_ID);
            throw new InvalidArgumentsException(MessageConstants.MESSAGE_INVALID_ID);
        }
        if (price != null) {
            checkIfPriceValid(price);
            Double.parseDouble(price);
        }
        if (category != null) {
            category = category.toUpperCase();
            try {
                Category.valueOf(category);
            } catch (IllegalArgumentException e) {
                logger.warning("Category does not exist: " + MessageConstants.MESSAGE_INVALID_CATEGORY);
                throw new InvalidArgumentsException(MessageConstants.MESSAGE_INVALID_CATEGORY);
            }
        }
        if (description != null) {
            checkIfDescriptionValid(description);
        }
        logger.exiting(Parser.class.getName(), "parseEditCommand()");
        return new EditCommand(expenseId, description, category, price);
    }


    /**
     * Returns a ViewCommand object to be executed by the backend. The object may
     * contain the user specified view count, as well as other optional flags such
     * as date or price ranges. Any missing required inputs or incorrect formats
     * will be raised to the user via the UI.
     *
     * @param arguments User input string after the view command.
     * @return Command ViewCommand object to be executed by backend.
     * @throws InvalidArgumentsException If required arguments are in incorrect format.
     * @throws InvalidCategoryException  If specified category does not exist.
     * @throws InvalidDateException      If specified date does not exist.
     * @throws MissingDateException      If required end/start date is not specified.
     */
    private Command parseViewCommand(String arguments) throws InvalidArgumentsException, InvalidCategoryException,
            InvalidDateException, MissingDateException, UnknownOptionException, DuplicateOptionException, MissingArgumentsException {
        logger.entering(Parser.class.getName(), "parseViewCommand()");
        logger.info("Parsing view command with arguments: " + arguments);
        if (arguments.isEmpty()) {
            logger.info("No count specified. Listing all expenses");
            return new ViewCommand(Integer.MAX_VALUE);
        }
        checkDuplicateOptionsView(arguments);
        checkUnknownOption(arguments, availableOptionsView);
        String[] argumentsArray = arguments.split(" ");
        assert argumentsArray.length >= 1 : "User input must contain at least 1 argument";
        Category category = null;
        String categoryStr = extractDetail(arguments, CATEGORY_PATTERN);
        if (categoryStr != null) {
            category = CategoryUtil.convertStringToCategory(StringUtil.toTitleCase(categoryStr));
        }
        Double[] startEndPrices = extractPrices(arguments);
        Double priceMinDouble = startEndPrices[0];
        Double priceMaxDouble = startEndPrices[1];
        Integer viewCountInt = extractViewCount(arguments);
        String[] startEndDates = extractDates(arguments);
        String startDateString = startEndDates[0];
        String endDateString = startEndDates[1];
        logger.info("User entered count:" + viewCountInt);
        logger.info("User entered category:" + categoryStr);
        logger.info("User entered start date: " + startDateString);
        logger.info("User entered end date: " + endDateString);
        logger.exiting(Parser.class.getName(), "parseViewCommand()");
        return new ViewCommand(viewCountInt, category, priceMinDouble, priceMaxDouble, startDateString, endDateString);
    }


    /**
     * Returns the view count if specified by the user. If not specified, the max
     * integer is returned to list all expenses.
     *
     * @param arguments User input after view command.
     * @return Integer View count to be displayed to the user.
     * @throws InvalidArgumentsException If count specified is not a positive
     *                                   integer.
     */
    private Integer extractViewCount(String arguments) throws InvalidArgumentsException, MissingArgumentsException {
        String viewCount = extractDetail(arguments, ID_PATTERN); //detail extracted is either view count or an
        // optional flag indicated by user
        if (viewCount.matches("-sd|-sp|-ep|-c|-startdate|-enddate|-category|-startprice|-endprice")) {
            //count is not specified
            viewCount = Integer.toString(Integer.MAX_VALUE);
        }
        Integer viewCountInt;
        try {
            viewCountInt = Integer.parseInt(viewCount);
        } catch (NumberFormatException e) {
            logger.warning("Expense ID is not an integer: " + MessageConstants.MESSAGE_INVALID_ID);
            throw new InvalidArgumentsException(MessageConstants.MESSAGE_INVALID_ID);
        }
        if (viewCountInt < 0) {
            logger.warning("Negative expense ID provided: " + MessageConstants.MESSAGE_INVALID_ID);
            throw new InvalidArgumentsException(MessageConstants.MESSAGE_INVALID_ID);
        }
        return viewCountInt;
    }


    /**
     * Returns the start and end prices specified by the user when using the filter
     * by price feature. If both not specified, the entire price range is displayed.
     * If only the starting price is specified, all expenses above that price is
     * displayed.
     *
     * @param arguments User input string after view command.
     * @return Double[] Array containing start and end price respectively.
     * @throws InvalidArgumentsException If price specified is not in numerical form
     *                                   or if range specified is invalid.
     */
    private Double[] extractPrices(String arguments) throws InvalidArgumentsException, MissingArgumentsException {
        Double[] prices = new Double[2];
        String priceMinStr = extractDetail(arguments, START_PRICE_PATTERN);
        arguments = arguments.replaceFirst(PRICE_FLAG + priceMinStr, ""); //Remove starting price from string
        String priceMaxStr = extractDetail(arguments, END_PRICE_PATTERN);
        if (priceMinStr != null) {
            checkIfPriceValid(priceMinStr);
        } else {
            priceMinStr = "0";
        }
        if (priceMaxStr != null) {
            checkIfPriceValid(priceMaxStr);
        } else {
            priceMaxStr = Integer.toString(Integer.MAX_VALUE);
        }
        Double priceMinDouble = Double.parseDouble(priceMinStr);
        Double priceMaxDouble = Double.parseDouble(priceMaxStr);
        if (priceMaxDouble < priceMinDouble) {
            logger.warning("Maximum price range higher than minimum: " + MessageConstants.MESSAGE_INVALID_PRICE_RANGE);
            throw new InvalidArgumentsException(MessageConstants.MESSAGE_INVALID_PRICE_RANGE);
        }
        prices[0] = priceMinDouble;
        prices[1] = priceMaxDouble;
        return prices;
    }


    /**
     * Returns the start and end dates specified by the user when using the filter
     * by date feature. Both dates have to specified if user uses this feature. If
     * both not specified, all expenses are displayed.
     *
     * @param arguments User input string after view command.
     * @return String[] Array containing start and end date respectively.
     * @throws InvalidDateException If date specified does not exist.
     * @throws MissingDateException If either start or end date is not specified.
     */
    private String[] extractDates(String arguments) throws InvalidDateException, MissingDateException, MissingArgumentsException {
        String[] dates = new String[2];
        String startDateString = extractDetail(arguments, START_DATE_PATTERN);
        String endDateString = extractDetail(arguments, END_DATE_PATTERN);
        if (startDateString != null) {
            logger.info("start date identified as: " + startDateString);
            isValidDate(startDateString);
            logger.info("start date verified");
            startDateString = startDateString + EntryConstants.EARLIEST_TIME;
        }
        if (endDateString != null) {
            logger.info("end date identified as: " + endDateString);
            isValidDate(endDateString);
            logger.info("end date verified");
            endDateString = endDateString + EntryConstants.LATEST_TIME;
        }
        if (startDateString == null ^ endDateString == null) {
            logger.info("Missing at least one date as view command request parameter");
            throw new MissingDateException(MessageConstants.MESSAGE_MISSING_DATE);
        }
        dates[0] = startDateString;
        dates[1] = endDateString;
        return dates;
    }


    /**
     * Returns group two of the matched pattern. This group is specified to be the
     * detail we wish to extract.
     *
     * @param string User entered input after the command.
     * @param detail The Pattern that is to be matched by the user input.
     * @return String The matched string.
     */
    private String extractDetail(String string, Pattern detail) throws MissingArgumentsException {
        String detailToExtract;
        Matcher matcher = detail.matcher(string);
        if (matcher.find()) { //option was used
            String option = matcher.group(1);
            detailToExtract = matcher.group(2);
            if (detailToExtract == null || detailToExtract.trim().isEmpty()) { //missing argument for option used
                throw new MissingArgumentsException(MessageConstants.MESSAGE_MISSING_ARGUMENT + option);
            }
            return detailToExtract.trim();
        }
        return null;
    }


    /**
     * Checks if the description of the expense specified by the user only contains
     * letters, numbers and spaces.
     *
     * @param description User specified description of the expense.
     * @throws InvalidArgumentsException If description is in incorrect format.
     */
    private void checkIfDescriptionValid(String description) throws InvalidArgumentsException {
        boolean containsComma = description.contains(",");
        if (containsComma) {
            throw new InvalidArgumentsException(MessageConstants.MESSAGE_INVALID_DESCRIPTION);
        }
    }


    /**
     * Checks if price is only in integer or decimal form.
     *
     * @param price User specified price of the expense.
     * @throws InvalidArgumentsException If price is in incorrect format.
     */
    private void checkIfPriceValid(String price) throws InvalidArgumentsException {
        boolean isValid = price.matches(VALID_PRICE_REGEX);
        if (!isValid) {
            throw new InvalidArgumentsException(MessageConstants.MESSAGE_INVALID_PRICE);
        }
    }

    private int countOption(String input, String option) {
        int count = 0;
        String[] arguments = input.split("\\s+");
        for (String argument : arguments) {
            if (argument.matches(option)) {
                count++;
            }
        }
        return count;
    }

    private void checkDuplicateOptionsAddEdit(String input) throws DuplicateOptionException {
        int descriptionOptionCount = countOption(input, descriptionOption);
        int priceOptionCount = countOption(input, priceOption);
        int categoryOptionCount = countOption(input, categoryOption);
        if (descriptionOptionCount > 1 || priceOptionCount > 1 || categoryOptionCount > 1) {
            throw new DuplicateOptionException(MessageConstants.MESSAGE_DUPLICATE_OPTION);
        }
    }

    private void checkUnknownOption(String input, String options) throws UnknownOptionException {
        String[] arguments = input.split("\\s+");
        for (String argument : arguments) {
            char firstCharacter = argument.charAt(0);
            if (firstCharacter == '-' && !argument.matches(options)) { //option not recognised
                throw new UnknownOptionException(MessageConstants.MESSAGE_UNKNOWN_OPTION + argument);
            }
        }
    }

    private void checkDuplicateOptionsView(String input) throws DuplicateOptionException {
        checkDuplicateOptionsAddEdit(input);
        int startpriceOptionCount = countOption(input, startpriceOption);
        int endpriceOptionCount = countOption(input, endpriceOption);
        int startdateOptionCount = countOption(input, startdateOption);
        int enddateOptionCount = countOption(input, enddateOption);
        if (startpriceOptionCount > 1 || endpriceOptionCount > 1 || startdateOptionCount > 1 || enddateOptionCount > 1) {
            throw new DuplicateOptionException(MessageConstants.MESSAGE_DUPLICATE_OPTION);
        }
    }

    /**
     * Checks if date is valid.
     *
     * @param dateString User specified date.
     * @throws InvalidDateException If date is invalid.
     */
    private void isValidDate(String dateString) throws InvalidDateException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
        try {
            simpleDateFormat.setLenient(false);
            Date testDate = simpleDateFormat.parse(dateString);
        } catch (java.text.ParseException e) {
            logger.warning("Invalid date entered: " + MessageConstants.MESSAGE_INVALID_DATE);
            throw new InvalidDateException(MessageConstants.MESSAGE_INVALID_DATE);
        }
    }
}
// @@author
