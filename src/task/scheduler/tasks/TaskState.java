package task.scheduler.tasks;

public enum TaskState {
    NOTSTARTED, RUNNING, RUNNINGWITHPAUSEREQUEST, WAITINGTORESUME, PAUSED, FINISHED;
}
