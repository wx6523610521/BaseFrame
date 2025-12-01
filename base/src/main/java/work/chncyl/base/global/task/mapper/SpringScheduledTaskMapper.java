package work.chncyl.base.global.task.mapper;

import work.chncyl.base.global.task.SpringScheduledTask;

import java.util.List;

public interface SpringScheduledTaskMapper {

    /**
     * 插入任务
     * @param task 任务实体
     * @return 影响行数
     */
    int insert(SpringScheduledTask task);

    /**
     * 更新任务
     * @param task 任务实体
     * @return 影响行数
     */
    int updateById(SpringScheduledTask task);

    /**
     * 根据ID删除任务
     * @param id 任务ID
     * @return 影响行数
     */
    int deleteById(String id);

    /**
     * 根据ID查询任务
     * @param id 任务ID
     * @return 任务实体
     */
    SpringScheduledTask selectById(String id);

    /**
     * 查询所有任务
     * @return 任务列表
     */
    List<SpringScheduledTask> selectList();

    /**
     * 根据状态查询任务列表
     * @param status 状态
     * @return 任务列表
     */
    List<SpringScheduledTask> selectByStatus(Integer status);

    /**
     * 根据任务名称查询任务
     * @param taskName 任务名称
     * @return 任务
     */
    SpringScheduledTask selectByTaskName(String taskName);
}