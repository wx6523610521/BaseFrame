package work.chncyl.base.global.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import work.chncyl.base.global.task.service.inter.ISpringScheduledTaskService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 动态定时任务配置器
 * @Author: chncyl
 * @Date: 2025-01-01
 * @Version: V1.0
 */
@Service
@Configuration
public class MySchedulingConfigurer implements SchedulingConfigurer {

    @Autowired
    private ISpringScheduledTaskService springScheduledTaskService;

    @Autowired
    private DynamicTaskManager dynamicTaskManager;

    // Store scheduled tasks with their metadata
    private static final Map<String, ScheduledTaskMetadata> scheduledTasks = new HashMap<>();

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 初始化动态任务管理器
        dynamicTaskManager.configureTasks(taskRegistrar);

        // 应用启动时初始化所有启用的任务
        springScheduledTaskService.initAllTasks();

        System.out.println("动态定时任务配置器初始化完成，时间：" + LocalDateTime.now());
    }

    /**
     * Add a new scheduled task
     *
     * @param taskName       Unique identifier for the task
     * @param cronExpression Cron expression for scheduling
     * @param task           Runnable containing the method to execute
     */
    public static void addScheduledTask(String taskName, String cronExpression, Runnable task) {
        // Store task metadata
        ScheduledTaskMetadata metadata = new ScheduledTaskMetadata(taskName, cronExpression, task);
        scheduledTasks.put(taskName, metadata);

        // Here you would typically register this with Spring's scheduler
        // For now, we'll just store it for later use
    }

    /**
     * Modify an existing scheduled task
     *
     * @param taskName          Name of the task to modify
     * @param newCronExpression New cron expression
     * @param newTask           New runnable task
     */
    public static void modifyScheduledTask(String taskName, String newCronExpression, Runnable newTask) {
        if (scheduledTasks.containsKey(taskName)) {
            ScheduledTaskMetadata metadata = scheduledTasks.get(taskName);
            metadata.setCronExpression(newCronExpression);
            metadata.setTask(newTask);

            // Here you would typically update the actual scheduled task
            // This would require integration with Spring's TaskScheduler
        }
    }

    /**
     * Get all scheduled tasks
     */
    public static Map<String, ScheduledTaskMetadata> getScheduledTasks() {
        return new HashMap<>(scheduledTasks);
    }

    /**
     * Inner class to hold task metadata
     */
    public static class ScheduledTaskMetadata {
        private String taskName;
        private String cronExpression;
        private Runnable task;

        public ScheduledTaskMetadata(String taskName, String cronExpression, Runnable task) {
            this.taskName = taskName;
            this.cronExpression = cronExpression;
            this.task = task;
        }

        // Getters and setters
        public String getTaskName() {
            return taskName;
        }

        public String getCronExpression() {
            return cronExpression;
        }

        public Runnable getTask() {
            return task;
        }

        public void setCronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
        }

        public void setTask(Runnable task) {
            this.task = task;
        }
    }
}