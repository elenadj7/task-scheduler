package task.scheduler.algorithms;

import task.scheduler.tasks.TaskContext;

public abstract class SchedulingAlgorithm {
    public abstract void add(TaskContext taskContext);

    public abstract TaskContext remove();

    public abstract int size();
}
