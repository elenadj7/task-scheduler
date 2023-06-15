package task.scheduler.algorithms;

import task.scheduler.tasks.TaskContext;

import java.util.PriorityQueue;

public class PrioritySchedulingAlgorithm extends SchedulingAlgorithm {
    private final PriorityQueue<TaskContext> queue = new PriorityQueue<>();

    @Override
    public synchronized void add(TaskContext taskContext) {
        queue.add(taskContext);
    }

    @Override
    public synchronized TaskContext remove() {
        return queue.poll();
    }

    @Override
    public synchronized int size() {
        return queue.size();
    }

    @Override
    public String toString() {
        return "PrioritySchedulingAlgorithm";
    }
}
