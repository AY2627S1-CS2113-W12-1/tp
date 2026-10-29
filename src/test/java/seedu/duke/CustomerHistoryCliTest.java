package seedu.duke;

//@@author anannya-mk

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the customer and history commands through the application input and output.
 */
public class CustomerHistoryCliTest {
    private static final String ADD_ALICE =
            "add n/Alice Tan p/91234567 d/22-10-2030 s/8 f/Chocolate fi/Strawberry\n";
    private static final String ADD_BOB =
            "add n/Bob Lim p/81234567 d/20-10-2030 s/6 f/Vanilla fi/Cream\n";

    @TempDir
    private Path tempDir;

    @Test
    public void run_addOrders_reportsNewAndReturningCustomers() {
        String result = runSession(ADD_ALICE + ADD_BOB + ADD_ALICE + "bye\n");
        assertTrue(result.contains("Customer ID: C001 (new customer)"));
        assertTrue(result.contains("Customer ID: C002 (new customer)"));
        assertTrue(result.contains("Customer ID: C001 (returning customer)"));
    }

    @Test
    public void run_customerAndHistory_showDetailsAndOrders() {
        String result = runSession(ADD_ALICE + ADD_BOB + ADD_ALICE
                + "cancel O003\ncustomer c001\nhistory C001\nbye\n");
        assertTrue(result.contains("Name: Alice Tan"));
        assertTrue(result.contains("Orders: 2"));
        assertTrue(result.contains("Repeat customer: Yes"));
        assertTrue(result.contains("Order history for Alice Tan (C001):"));
        assertTrue(result.contains("2. O003"));
        assertTrue(result.contains("[CANCELLED]"));
    }

    @Test
    public void run_customerIdsSurviveRestart() {
        runSession(ADD_ALICE + ADD_BOB + "bye\n");
        String result = runSession("customer C002\nbye\n");
        assertTrue(result.contains("Name: Bob Lim"));
    }

    @Test
    public void run_viewOrder_showsCustomerId() {
        String result = runSession(ADD_ALICE + "view O001\nbye\n");
        assertTrue(result.contains("Order ID: O001"));
        assertTrue(result.contains("Customer ID: C001"));
    }

    @Test
    public void run_editPhoneThenRestart_customerIdsDoNotChange() {
        runSession(ADD_ALICE + ADD_BOB + "edit O001 p/90000000\nbye\n");
        String result = runSession("customer C002\ncustomer C001\n" + ADD_ALICE + "bye\n");
        assertTrue(result.contains("Name: Bob Lim"));
        assertTrue(result.contains("Phone: 90000000"));
        assertTrue(result.contains("Customer ID: C003 (new customer)"));
    }

    @Test
    public void run_badCustomerCommands_printErrors() {
        String result = runSession("customer C999\nhistory C999\ncustomer\nhistory X1\nbye\n");
        assertTrue(result.contains("Error: Customer not found: C999"));
        assertTrue(result.contains("Error: Use: customer CUSTOMER_ID"));
        assertTrue(result.contains("Error: Use: history CUSTOMER_ID"));
    }

    private String runSession(String commands) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            new Duke(tempDir.resolve("cakequeue.txt")).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
