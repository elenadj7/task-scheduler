package task.scheduler.algorithms;

import task.scheduler.tasks.TaskContext;

import java.util.LinkedList;

public class FifoSchedulingAlgorithm extends SchedulingAlgorithm{
    private final LinkedList<TaskContext> queue = new LinkedList<>();
    @Override
    public synchronized void add(TaskContext taskContext) {
        queue.addLast(taskContext);
    }
    @Override
    public synchronized TaskContext remove() {
        return queue.pollFirst();
    }

    @Override
    public synchronized int size() {
        return queue.size();
    }
}
