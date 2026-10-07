package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Menu;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

class DukeTest {
    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void deleteOrder_removesTheChosenOrder() {
        Menu menu = new Menu();
        menu.add(new Cake("Iced latte", "coffee", 5.00));
        menu.add(new Cake("Chocolate cake", "chocolate", 25.00));

        Customer alex = new Customer("Alex", "91234567");
        OrderList orders = new OrderList();
        orders.add(new Order(alex, menu.get(1), 1));
        orders.add(new Order(alex, menu.get(2), 1));

        Order removed = orders.deleteOrder(1);

        assertEquals("Iced latte", removed.getCake().getName());
        assertEquals(1, orders.size());
    }
}
