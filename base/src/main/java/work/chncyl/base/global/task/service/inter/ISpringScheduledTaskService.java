package work.chncyl.base.global.task.service.inter;

import work.chncyl.base.global.task.SpringScheduledTask;

import java.util.List;

/**
 * @Description: Spring动态定时任务服务接口
 * @Author: chncyl
 * @Date: 2025-01-01
 * @Version: V1.0
 */
public interface ISpringScheduledTaskService {

    /**
     * 添加定时任务
     * @param task 任务实体
     * @return 是否成功
     */
    boolean addTask(SpringScheduledTask task);

    /**
     * 更新定时任务
     * @param task 任务实体
     * @return 是否成功
     */
    boolean updateTask(SpringScheduledTask task);

    /**
     * 根据ID删除定时任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean deleteTask(String taskId);

    /**
     * 根据任务名称删除定时任务
     * @param taskName 任务名称
     * @return 是否成功
     */
    boolean deleteTaskByName(String taskName);

    /**
     * 根据ID查询定时任务
     * @param taskId 任务ID
     * @return 任务实体
     */
    SpringScheduledTask getTaskById(String taskId);

    /**
     * 根据任务名称查询定时任务
     * @param taskName 任务名称
     * @return 任务实体
     */
    SpringScheduledTask getTaskByName(String taskName);

    /**
     * 获取所有定时任务
     * @return 任务列表
     */
    List<SpringScheduledTask> getAllTasks();

    /**
     * 根据状态查询定时任务
     * @param status 状态
     * @return 任务列表
     */
    List<SpringScheduledTask> getTasksByStatus(Integer status);

    /**
     * 启动定时任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean startTask(String taskId);

    /**
     * 停止定时任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean stopTask(String taskId);

    /**
     * 立即执行定时任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean executeTask(String taskId);

    /**
     * 更新任务调度时间
     * @param taskId 任务ID
     * @return 是否成功
     */
    public boolean updateTaskCron(String taskId, String cronExpression);

    /**
     * 更新任务执行方法
     * @param taskId 任务ID
     * @param taskClass 任务类名
     * @param taskMethod 任务方法名
     * @param taskParameter 任务参数
     * @return 是否成功
     */
    boolean updateTaskMethod(String taskId, String taskClass, String taskMethod, String taskParameter);

    /**
     * 初始化所有任务（应用启动时调用）
     */
    void initAllTasks();
}