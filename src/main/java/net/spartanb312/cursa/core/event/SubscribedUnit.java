package net.spartanb312.cursa.core.event;

import net.spartanb312.cursa.core.concurrent.task.ObjectTask;

public class SubscribedUnit {

    private final Object owner;
    private final Class<?> eventClass;
    private final ObjectTask task;
    private final int priority;

    public SubscribedUnit(Object owner, Class<?> eventClass, ObjectTask task, int priority) {
        this.owner = owner;
        this.eventClass = eventClass;
        this.task = task;
        this.priority = priority;
    }

    public ObjectTask getTask() {
        return task;
    }

    public Class<?> getEventClass() {
        return eventClass;
    }

    public Object getOwner() {
        return owner;
    }

    public int getPriority() {
        return priority;
    }

}
