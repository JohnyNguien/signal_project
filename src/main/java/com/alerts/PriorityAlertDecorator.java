package com.alerts;

/**
 * Tags the wrapped Alert with a priority level (e.g. for urgent routing).
 */
public class PriorityAlertDecorator extends AlertDecorator {
    private final int priorityLevel;

    public PriorityAlertDecorator(Alert wrapped, int priorityLevel) {
        super(wrapped);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public void send() {
        System.out.printf("[PRIORITY %d] ", priorityLevel);
        super.send();
    }
}
