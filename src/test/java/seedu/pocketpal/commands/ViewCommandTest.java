package seedu.pocketpal.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seedu.pocketpal.backend.BackendTestUtil;
import seedu.pocketpal.data.entry.Category;
import seedu.pocketpal.data.entry.Entry;
import seedu.pocketpal.data.entrylog.EntryLog;
import seedu.pocketpal.frontend.commands.ViewCommand;
import seedu.pocketpal.frontend.constants.UIConstants;
import seedu.pocketpal.frontend.ui.UI;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Test view command")
public class ViewCommandTest extends BackendTestUtil {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final Entry testEntry1 = new Entry("Macdonalds", 7.50, Category.FOOD);
    private final Entry testEntry2 = new Entry("Noodles", 9.40, Category.FOOD);
    private final Entry testEntry3 = new Entry("Cab", 10.80, Category.TRANSPORTATION);
    private final UI ui = new UI();
    private EntryLog testEntries = new EntryLog();
    @BeforeEach
    void init(){
        testEntries.clearAllEntries();
        TEST_BACKEND.clearData();
        testEntries.addEntry(testEntry1);
        testEntries.addEntry(testEntry2);
        testEntries.addEntry(testEntry3);
        addEntry(testEntry1);
        addEntry(testEntry2);
        addEntry(testEntry3);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    @DisplayName("Test view by price range")
    void testViewByPriceRange(){
        try{
            ViewCommand testCommand = assertDoesNotThrow(() -> new ViewCommand(Integer.MAX_VALUE, null, 7.00, 10.50));
            testCommand.execute(TEST_UI, TEST_BACKEND);
            double expectedTotalPrice = 0;
            for (int index = 1; index <= 2; index++) {
                expectedTotalPrice += testEntries.getEntry(index).getAmount();
            }
            StringBuilder expectedString = new StringBuilder();
            expectedString.append("These are the latest ")
                    .append((testEntries.getSize()) - 1)
                    .append(" entries.")
                    .append(System.lineSeparator());
            expectedString.append("Total expenditure: $" + expectedTotalPrice).append(System.lineSeparator());
            for (int index = 1; index <= 2; index ++){
                String formattedEntry = ui.formatViewEntries(testEntries.getEntry(index), index);
                expectedString.append(formattedEntry).append(System.lineSeparator());
            }
            expectedString.append(UIConstants.LINE);
            assertEquals(expectedString.toString(), outContent.toString());
        } catch (Exception e){
            fail("Unexpected exception");
        }
    }
}