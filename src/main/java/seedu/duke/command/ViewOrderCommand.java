package seedu.duke.command;

import seedu.duke.model.OrderList;

public class ViewOrderCommand {
    private final int index;

    public ViewOrderCommand(int index) {
        this.index = index;
    }

    public String execute(OrderList orders) {
        return orders.get(index).toDetailedString();
    }
}
