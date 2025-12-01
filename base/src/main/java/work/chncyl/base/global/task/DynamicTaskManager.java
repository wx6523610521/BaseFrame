package work.chncyl.base.global.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @Description: 动态任务管理器
 * @Author: chncyl
 * @Date: 2025-01-01
 * @Version: V1.0
 */
@Slf4j
@Component
public class DynamicTaskManager implements SchedulingConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    // 存储动态任务
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private ScheduledTaskRegistrar taskRegistrar;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }

    /**
     * 添加动态任务
     *
     * @param task 任务配置
     */
    public void addTask(SpringScheduledTask task) {
        try {
            // 如果任务已存在，先移除
            removeTask(task.getTaskName());

            // 创建任务
            Runnable runnable = createRunnable(task);
            if (runnable == null) {
                log.error("创建任务失败，无法找到对应的方法: {}.{}", task.getTaskClass(), task.getTaskMethod());
                return;
            }

            // 注册任务
            ScheduledFuture<?> future = taskRegistrar.getScheduler().schedule(
                    runnable,
                    triggerContext -> {
                        CronTrigger trigger = new CronTrigger(task.getCronExpression());
                        return trigger.nextExecutionTime(triggerContext);
                    }
            );

            scheduledTasks.put(task.getTaskName(), future);
            log.info("动态任务添加成功: {} - {}", task.getTaskName(), task.getCronExpression());
        } catch (Exception e) {
            log.error("添加动态任务失败: {}", task.getTaskName(), e);
            throw new RuntimeException("添加动态任务失败", e);
        }
    }

    /**
     * 更新任务
     *
     * @param task 任务配置
     */
    public void updateTask(SpringScheduledTask task) {
        removeTask(task.getTaskName());
        addTask(task);
    }

    /**
     * 更新任务调度时间
     *
     * @param taskName       任务名称
     * @param cronExpression cron表达式
     */
    public void updateTaskCron(String taskName, String cronExpression) {
        // 先移除旧任务，然后重新添加
        removeTask(taskName);
        // 这里需要从数据库重新加载任务配置，然后更新cron表达式后重新添加
        // 这个功能需要在服务层实现
    }

    /**
     * 更新任务执行方法
     *
     * @param task 任务配置
     */
    public void updateTaskMethod(SpringScheduledTask task) {
        updateTask(task);
    }

    /**
     * 移除动态任务
     *
     * @param taskName 任务名称
     */
    public void removeTask(String taskName) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskName);
        if (future != null) {
            future.cancel(true);
            log.info("动态任务移除成功: {}", taskName);
        }
    }

    /**
     * 立即执行任务
     *
     * @param task 任务配置
     */
    public void executeTaskImmediately(SpringScheduledTask task) {
        try {
            Runnable runnable = createRunnable(task);
            if (runnable != null) {
                runnable.run();
                log.info("立即执行任务成功: {}", task.getTaskName());
            }
        } catch (Exception e) {
            log.error("立即执行任务失败: {}", task.getTaskName(), e);
            throw new RuntimeException("立即执行任务失败", e);
        }
    }

    /**
     * 创建Runnable任务
     *
     * @param task 任务配置
     * @return Runnable实例
     */
    private Runnable createRunnable(SpringScheduledTask task) {
        return () -> {
            try {
                // 获取任务类
                Class<?> clazz = Class.forName(task.getTaskClass());
                Object bean = applicationContext.getBean(clazz);

                // 获取任务方法
                Method method;
                if (task.getTaskParameter() != null && !task.getTaskParameter().isEmpty()) {
                    // 如果有参数，使用带参数的方法
                    method = clazz.getMethod(task.getTaskMethod(), String.class);
                    method.invoke(bean, task.getTaskParameter());
                } else {
                    // 无参数方法
                    method = clazz.getMethod(task.getTaskMethod());
                    method.invoke(bean);
                }

                log.debug("动态任务执行成功: {}.{}", task.getTaskClass(), task.getTaskMethod());
            } catch (Exception e) {
                log.error("动态任务执行失败: {}.{}", task.getTaskClass(), task.getTaskMethod(), e);
            }
        };
    }

    /**
     * 获取所有动态任务
     *
     * @return 任务名称集合
     */
    public Map<String, ScheduledFuture<?>> getScheduledTasks() {
        return new ConcurrentHashMap<>(scheduledTasks);
    }

    /**
     * 检查任务是否存在
     *
     * @param taskName 任务名称
     * @return 是否存在
     */
    public boolean containsTask(String taskName) {
        return scheduledTasks.containsKey(taskName);
    }

    /**
     * 停止所有动态任务
     */
    public void stopAllTasks() {
        for (Map.Entry<String, ScheduledFuture<?>> entry : scheduledTasks.entrySet()) {
            entry.getValue().cancel(true);
            log.info("停止动态任务: {}", entry.getKey());
        }
        scheduledTasks.clear();
    }
}