package task.scheduler.tasks;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class TaskContext implements Comparable<TaskContext>{
    private final ITask task;

    public TaskState getTaskState() {
        return taskState;
    }

    private TaskState taskState = TaskState.NOTSTARTED;
    private final int priority;
    private final Thread thread;
    private final Object toLock = new Object();
    private final Consumer<TaskContext> onTaskFinished;
    private final Consumer<TaskContext> onTaskPaused;
    private final Consumer<TaskContext> onTaskContinueRequested;
    private final Semaphore finishedSemaphore = new Semaphore(0);
    private final Semaphore resumeSemaphore = new Semaphore(0);
    private int numberOfWaiters = 0;
    public TaskContext(ITask task, Consumer<TaskContext> onTaskFinished, Consumer<TaskContext> onTaskPaused, Consumer<TaskContext> onTaskContinueRequested, int priority){
        this.task = task;
        this.onTaskFinished = onTaskFinished;
        this.onTaskPaused = onTaskPaused;
        this.onTaskContinueRequested = onTaskContinueRequested;
        this.priority = priority;
        thread = new Thread(() -> {
            try {
                task.run(this);
            } finally {
                finish();
            }
        });
    }
    public ITask getTask(){
        return task;
    }
    public int getPriority() {
        return priority;
    }
    private void finish(){

        synchronized (toLock){
            switch (taskState){
                case NOTSTARTED -> {
                    throw new IllegalStateException("Task not started");
                }
                case RUNNINGWITHPAUSEREQUEST -> {

                }
                case RUNNING -> {
                    taskState = TaskState.FINISHED;
                    if(numberOfWaiters > 0){
                        finishedSemaphore.release(numberOfWaiters);
                    }
                    onTaskFinished.accept(this);
                }
                case FINISHED -> {
                    throw new IllegalStateException("Task already finished");
                }
                default -> {
                    throw new IllegalStateException("Invalid task state");
                }
            }
        }
    }
    public void start(){
        synchronized (toLock){
            switch (taskState){
                case NOTSTARTED -> {
                    taskState = TaskState.RUNNING;
                    thread.start();
                }
                case RUNNINGWITHPAUSEREQUEST -> {

                }
                case RUNNING -> {
                    throw new IllegalStateException("Task already started");
                }
                case FINISHED -> {
                    throw new IllegalStateException("Task already finished");
                }
                case WAITINGTORESUME -> {
                    taskState = TaskState.RUNNING;
                    resumeSemaphore.release();
                }
                default -> {
                    throw new IllegalStateException("Invalid task state");
                }
            }
        }
    }
    public void waitToExecute() throws InterruptedException {
        synchronized (toLock){
            switch (taskState){
                case NOTSTARTED, RUNNINGWITHPAUSEREQUEST -> {

                }
                case RUNNING -> {
                    numberOfWaiters++;
                }
                case FINISHED -> {
                    return;
                }
                default -> {
                    throw new IllegalStateException("Invalid task state");
                }
            }
        }
        finishedSemaphore.acquire();
    }

    public void requestPause(){
        synchronized (toLock){
            switch (taskState){
                case NOTSTARTED -> {
                    throw new IllegalStateException("Task not started");
                }
                case RUNNING -> {
                    taskState = TaskState.RUNNINGWITHPAUSEREQUEST;
                }
                case RUNNINGWITHPAUSEREQUEST, FINISHED, PAUSED -> {

                }
                default -> throw new IllegalStateException("Invalid task state");
            }
        }
    }

    public void requestContinue(){
        synchronized (toLock){
            switch (taskState){
                case NOTSTARTED, FINISHED, RUNNING -> {

                }
                case RUNNINGWITHPAUSEREQUEST -> {
                    taskState = TaskState.RUNNING;
                }
                case PAUSED -> {
                    taskState = TaskState.WAITINGTORESUME;
                    onTaskContinueRequested.accept(this);
                }
                default -> throw new IllegalStateException("Invalid task state");
            }
        }
    }
    public void checkForPause() throws InterruptedException {
        boolean shouldPause = false;
        synchronized (toLock){
            switch (taskState){
                case NOTSTARTED, FINISHED -> {
                    throw new IllegalStateException("Invalid task state");
                }
                case RUNNING -> {

                }
                case RUNNINGWITHPAUSEREQUEST -> {
                    taskState = TaskState.PAUSED;
                    onTaskPaused.accept(this);
                    shouldPause = true;
                }
                default -> throw new IllegalStateException("Invalid task state");
            }
        }
        if(shouldPause){
            resumeSemaphore.acquire();
        }
    }

    @Override
    public int compareTo(TaskContext o) {
        return priority - o.priority;
    }
}
