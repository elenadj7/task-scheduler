package task.scheduler.tasks;

public interface ITask {

    void run(TaskContext taskContext);

    double getProgress();

    void setImmediateStart(boolean start);

    void setPriority(int priority);

    boolean getImmediateStart();

    int getPriority();
}
