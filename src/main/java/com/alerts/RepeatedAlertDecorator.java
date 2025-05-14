package com.alerts;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RepeatedAlertDecorator extends AlertDecorator {
    private final int repeatCount;
    private final long intervalMillis;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public RepeatedAlertDecorator(Alert wrapped, int repeatCount, long intervalMillis) {
        super(wrapped);
        this.repeatCount = repeatCount;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void send() {
        for (int attempt = 0; attempt < repeatCount; attempt++) {
            long delay = attempt * intervalMillis;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    wrapped.send();
                }
            };
            scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
        }
    }
}