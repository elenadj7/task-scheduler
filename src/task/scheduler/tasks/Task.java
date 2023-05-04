package task.scheduler.tasks;

import task.scheduler.processing.TaskScheduler;

public class Task {
    private final TaskContext taskContext;

    public Task(TaskContext taskContext){
        this.taskContext = taskContext;
    }

    public void waitToExecute() throws InterruptedException {
        taskContext.waitToExecute();
    }
    public void requestPause(){
        taskContext.requestPause();
    }
    public void requestContinue(){
        taskContext.requestContinue();
    }
    public void start(TaskScheduler scheduler){
        scheduler.scheduleTask(taskContext.getTask(), taskContext.getPriority());
    }
    public TaskContext getTaskContext() {
        return taskContext;
    }
}
