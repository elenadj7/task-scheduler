package task.scheduler.demo;

import task.scheduler.algorithms.FifoSchedulingAlgorithm;
import task.scheduler.algorithms.PrioritySchedulingAlgorithm;
import task.scheduler.processing.TaskScheduler;
import task.scheduler.tasks.Task;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args){
        TaskScheduler scheduler = new TaskScheduler(new PrioritySchedulingAlgorithm(), 3);
        ArrayList<Task> list = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < 10; ++i){
            if(i == 2 || i == 5 || i == 7 || i == 9){
                int pr = rand.nextInt(50);
                list.add(scheduler.scheduleWithoutStarting(new DemoTask(i * 1000, pr), pr));
            }
            int prio = rand.nextInt(50);
            scheduler.schedule(new DemoTask(prio), prio);
        }

       list.forEach(l -> l.start(scheduler));
    }
}
