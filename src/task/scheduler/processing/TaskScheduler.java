package task.scheduler.processing;

import task.scheduler.algorithms.SchedulingAlgorithm;
import task.scheduler.tasks.ITask;
import task.scheduler.tasks.Task;
import task.scheduler.tasks.TaskContext;

import java.util.HashSet;
import java.util.LinkedList;


public class TaskScheduler {
    private final int maxConcurrentTasks;
    private HashSet<TaskContext> runningTasks = new HashSet<>();
    private final SchedulingAlgorithm taskQueue;
    private final LinkedList<TaskContext> scheduledButNotStartedTasks = new LinkedList<>();
    private final Object toLock = new Object();

    public TaskScheduler(SchedulingAlgorithm taskQueue, int maxConcurrentTasks) {

        this.maxConcurrentTasks = maxConcurrentTasks;
        this.taskQueue = taskQueue;
    }

    public Task schedule(ITask task, int priority) {
        return handleSchedule(task, priority);
    }

    public Task schedule(ITask task) {
        return handleSchedule(task, 0);
    }

    private Task handleSchedule(ITask task, int priority) {
        TaskContext taskContext = new TaskContext(task, this::handleTaskFinished, this::handleTaskPaused, this::handleTaskContinueRequested, priority);
        synchronized (toLock) {
            if (runningTasks.size() < maxConcurrentTasks) {
                runningTasks.add(taskContext);
                taskContext.start();
            } else {
                taskQueue.add(taskContext);
            }
        }

        return new Task(taskContext);
    }

    private Task handleScheduleWithoutStarting(ITask task, int priority) {
        TaskContext taskContext = new TaskContext(task, this::handleTaskFinished, this::handleTaskPaused, this::handleTaskContinueRequested, priority);
        synchronized (toLock) {
            scheduledButNotStartedTasks.addLast(taskContext);
        }
        return new Task(taskContext);
    }

    public Task scheduleWithoutStarting(ITask task, int priority) {
        return handleScheduleWithoutStarting(task, priority);
    }

    public Task scheduleWithoutStarting(ITask task) {
        return handleScheduleWithoutStarting(task, 0);
    }

    public TaskContext scheduleTask(ITask task, int priority) {
        Task t = schedule(task, priority);
        synchronized (toLock) {
            scheduledButNotStartedTasks.remove(t.getTaskContext());
        }
        return t.getTaskContext();
    }

    private void handleTaskFinished(TaskContext taskContext) {
        synchronized (toLock) {
            runningTasks.remove(taskContext);
            if (taskQueue.size() > 0) {
                TaskContext firstTaskContext = taskQueue.remove();
                runningTasks.add(firstTaskContext);
                firstTaskContext.start();
            }
        }
    }

    private void handleTaskPaused(TaskContext taskContext) {
        synchronized (toLock) {
            runningTasks.remove(taskContext);
            if (taskQueue.size() > 0) {
                TaskContext firstTaskContext = taskQueue.remove();
                runningTasks.add(firstTaskContext);
                firstTaskContext.start();
            }
        }
    }

    private void handleTaskContinueRequested(TaskContext taskContext) {
        synchronized (toLock) {
            if (runningTasks.size() < maxConcurrentTasks) {
                runningTasks.add(taskContext);
                taskContext.start();
            } else {
                taskQueue.add(taskContext);
            }
        }
    }
}
