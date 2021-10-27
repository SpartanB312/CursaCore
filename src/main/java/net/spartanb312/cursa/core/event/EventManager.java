package net.spartanb312.cursa.core.event;

import net.spartanb312.cursa.core.concurrent.task.ObjectTask;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.spartanb312.cursa.core.concurrent.ConcurrentTaskManager.runBlocking;

/**
 * Author B_312
 * Created on 8/19/2021
 */
public class EventManager {

    private final List<SubscribedUnit> registeredUnits = new CopyOnWriteArrayList<>();
    private final List<SubscribedUnit> parallelUnits = new CopyOnWriteArrayList<>();

    public void register(Object owner) {
        unregister(owner);
        Method[] methods = owner.getClass().getDeclaredMethods();
        if (methods.length != 0) {
            AtomicBoolean normal = new AtomicBoolean(false);
            AtomicBoolean parallel = new AtomicBoolean(false);
            for (Method method : methods) {
                if (method.isAnnotationPresent(ParallelListener.class)) {
                    ParallelListener annotation = method.getAnnotation(ParallelListener.class);
                    SubscribedUnit unit = getUnit(method, owner, annotation.priority());
                    if (unit != null) {
                        registeredUnits.add(unit);
                        normal.set(true);
                    }
                } else if (method.isAnnotationPresent(Listener.class)) {
                    Listener annotation = method.getAnnotation(Listener.class);
                    SubscribedUnit unit = getUnit(method, owner, annotation.priority());
                    if (unit != null) {
                        parallelUnits.add(unit);
                        parallel.set(true);
                    }
                }
            }
            if (normal.get()) {
                registeredUnits.sort(Comparator.comparing(SubscribedUnit::getPriority));
                Collections.reverse(registeredUnits);
            }
            if (parallel.get()) {
                parallelUnits.sort(Comparator.comparing(SubscribedUnit::getPriority));
                Collections.reverse(parallelUnits);
            }
        }
    }

    private SubscribedUnit getUnit(Method method, Object owner, int priority) {
        if (method.getParameterCount() == 1) {
            if (!method.isAccessible()) method.setAccessible(true);
            ObjectTask task = it -> {
                try {
                    method.invoke(owner, it);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            return new SubscribedUnit(owner, method.getParameterTypes()[0], task, priority);
        }
        return null;
    }

    public void unregister(Object owner) {
        registeredUnits.removeIf(it -> it.getOwner().equals(owner));
        parallelUnits.removeIf(it -> it.getOwner().equals(owner));
    }

    public void post(Object event) {
        Class<?> eventClass = event.getClass();
        runBlocking(content -> {
            for (SubscribedUnit unit : parallelUnits) {
                if (eventClass.equals(unit.getEventClass())) {
                    content.launch(() -> unit.getTask().invoke(event));
                }
            }
            for (SubscribedUnit unit : registeredUnits) {
                if (eventClass.equals(unit.getEventClass())) {
                    unit.getTask().invoke(event);
                }
            }
        });
    }

    public boolean isRegistered(Object owner) {
        return registeredUnits.stream().anyMatch(it -> it.getOwner().equals(owner))
                || parallelUnits.stream().anyMatch(it -> it.getOwner().equals(owner));
    }

}
