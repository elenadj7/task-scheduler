package task.scheduler.demo;

import task.scheduler.tasks.ITask;
import task.scheduler.tasks.TaskContext;

public class DemoTask implements ITask {
    private static int instance = 0;
    private String name;
    public DemoTask(int priority){
        instance++;
        name = "Task" + instance + " - " + priority;
    }
    public DemoTask(int n, int priority){
        name = "Task" + n + " - " + priority;
    }
    @Override
    public void run(TaskContext taskContext) {
        System.out.println(name);

        try {
            taskContext.checkForPause();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
