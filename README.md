# Cursa Core

A collection of

1.ConcurrentTaskManager(Including runBlocking() addDelayTask() runRepeat() launch() repeat() runTiming() )

2.EventSystem(Decentralized and parallel runnable)

3.Settings and config container

4.Another utilities

## How to use ?

### EventSystem(Centralized)

A parallel runnable event system

```java
public class Test {
    public static EventManager EVENT_BUS = new EventManager();

    public static void main(String[] args) {
        //Create a object
        TestObject testObject = new TestObject();
        //Register the object
        EVENT_BUS.register(testObject);//unregister(object) to unregister the object 
        //Post an event
        EVENT_BUS.post(new TestEvent());
        //Check if the object is registered
        boolean isRegistered = EVENT_BUS.isRegistered(testObject);
    }
}
```   

```java
public class TestEvent {
    public String message;

    public TestEvent(String message) {
        this.message = message;
    }
}
```   

```java
public class TestObject {

    @Listener
    public void onEvent(TestEvent event) {
        System.out.println("Medium " + event.message);
    }

    @Listener(priority = Priority.HIGHEST)
    public void onEventHighestPriority(TestEvent event) {
        System.out.println("Highest " + event.message);
    }

    @ParallelListener(priority = Priority.HIGHEST)
    public void onEventParallel(TestEvent event) {
        System.out.println("Parallel " + event.message);
    }

}
```

### Launch

Launch a new thread

```java
public class Test {
    public static void main(String[] args) {
        launch(() -> {
            doTask();
            System.out.println("Finished in launch()!");
        });
        doTask();
        System.out.println("Finished in MainThread!");
    }

    public void doTask() {
        //assume we have a calculation task
        Thread.sleep(1000);
    }
}
```    

### RunBlocking

Running tasks in a content.You can launch child task in this content.At the end,the calling thread will be blocked until
all child tasks finished.

```java
public class Test {
    public static void main(String[] args) {
        runBlocking(content -> {
            //Task in MainThread
            doTask(100);
            content.launch(() -> {
                //Task in parallel thread1
                doTask(500);
            });
            content.launch(() -> {
                //Task in parallel thread2
                doTask(300);
            });
            //END: wait for all child tasks that launched in content are finished
        });
    }

    public void doTask(int time) {
        //assume we have a calculation task
        Thread.sleep(time);
    }
}
```

### addDelayTask

Add a scheduled delay task that will be executed in the future

```java
public class Test {
    public static void main(String[] args) {
        //Constant number
        addDelayTask(100, () -> {
            //After 100 ms will execute this
            doTask();
        });
        //By creating a DelayUnit
        DelayUnit unit = new DelayUnit(100, () -> {
            //After 100 ms will execute this
            doTask();
        });
        //Do something
        doTask();
        //Add delay task
        addDelayTask(unit);
    }

    public void doTask() {
        //assume we have a calculation task
        Thread.sleep(1000);
    }
}
```

### RunRepeat

Running a scheduled repeat task

```java
public class Test {
    //Setting for int supplier (String name,int default,int min,int max)
    Setting<Integer> delay = setting("Delay", 100, 0, 1000);

    public static void main(String[] args) {
        //every 100 ms will execute this(If the task takes time more than specified,it will invoke time out operation)
        runRepeat(100, () -> {
            //Do a task that takes 105ms
            doTask(105);
        }).timeOut(AfterTimeOutOperation.NONE, repeatUnit -> {
            //Do something
            doTask(100);
            //After finishing 1st task we suspend this repeatUnit
            repeatUnit.suspend();
            //Do something
            doTask(200);
            //After finishing 2nd task we resume this repeatUnit
            repeatUnit.resume();
        });
        //AfterTimeOutOperation.STOP will stop this repeat task.SUSPEND to suspend this repeatUnit.NONE to do nothing to the repeatUnit

        //Using int supplier
        runRepeat(() -> delay.getValue(), () -> {
            //Do something
            doTask(1000);
        });

        //By creating a repeatUnit
        RepeatUnit unit = new RepeatUnit(() -> delay.getValue(), () -> {
            //Do a task
            doTask(1000);
        }).timeOut(repeatUnit -> {
            //TimeOut warning
            System.out.println("Warning!");
        });

        //unit.suspend() to suspend this repeatUnit
        //unit.resume() to resume this repeatUnit
        //unit.stop() to stop this repeatUnit
    }

    public void doTask(int time) {
        //assume we have a calculation task
        Thread.sleep(time);
    }
}
```

### Repeat

Running repeat task in calling thread

```java
public class Test {
    public static void main(String[] args) {
        //Run 10 times doTask()
        repeat(10, () -> {
            //Do something
            doTask();
        });
    }

    public void doTask() {
        //assume we have a calculation task
        Thread.sleep(1000);
    }
}
```

### RumTiming

Record the time spent on a task

```java
public class Test {
    public static void main(String[] args) {
        long tookTime = runTiming(() -> {
            //Do something
            doTask();
        });
        System.out.println("Took " + tookTime + "ms to finish the task");
    }

    public void doTask() {
        //assume we have a calculation task
        Thread.sleep(1000);
    }
}
```
