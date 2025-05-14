package com.alerts;

/**
 * Base decorator—wraps any Alert and delegates all calls to it.
 */
public abstract class AlertDecorator extends Alert {
    protected final Alert wrapped;

    public AlertDecorator(Alert wrapped) {
        super(wrapped.getPatientId(),
              wrapped.getCondition(),
              wrapped.getTimestamp());
        this.wrapped = wrapped;
    }

    @Override
    public String getPatientId() {
        return wrapped.getPatientId();
    }

    @Override
    public String getCondition() {
        return wrapped.getCondition();
    }
    @Override
    public long getTimestamp() {
        return wrapped.getTimestamp();
    }

    @Override
    public void send() {
        wrapped.send();
    }
}
