package work.chncyl.base.global.task.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.chncyl.base.global.task.DynamicTaskManager;
import work.chncyl.base.global.task.SpringScheduledTask;
import work.chncyl.base.global.task.mapper.SpringScheduledTaskMapper;
import work.chncyl.base.global.task.service.inter.ISpringScheduledTaskService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description: Spring动态定时任务服务实现
 * @Author: chncyl
 * @Date: 2025-01-01
 * @Version: V1.0
 */
@Service
public class SpringScheduledTaskServiceImpl implements ISpringScheduledTaskService {

    private static final Logger log = LoggerFactory.getLogger(SpringScheduledTaskServiceImpl.class);

    @Autowired
    private SpringScheduledTaskMapper springScheduledTaskMapper;

    @Autowired
    private DynamicTaskManager dynamicTaskManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTask(SpringScheduledTask task) {
        try {
            // 检查任务名称是否已存在
            SpringScheduledTask existingTask = springScheduledTaskMapper.selectByTaskName(task.getTaskName());
            if (existingTask != null) {
                log.warn("任务名称已存在: {}", task.getTaskName());
                return false;
            }

            // 设置默认值
            if (task.getId() == null) {
                task.setId(UUID.randomUUID().toString().replace("-", ""));
            }
            if (task.getCreateTime() == null) {
                task.setCreateTime(new Date());
            }
            if (task.getUpdateTime() == null) {
                task.setUpdateTime(new Date());
            }
            if (task.getDelFlag() == null) {
                task.setDelFlag(0);
            }
            if (task.getStatus() == null) {
                task.setStatus(0); // 默认启用
            }

            int result = springScheduledTaskMapper.insert(task);
            if (result > 0) {
                // 如果任务状态为启用，则添加到调度器
                if (task.getStatus() == 0) {
                    dynamicTaskManager.addTask(task);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("添加定时任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("添加定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTask(SpringScheduledTask task) {
        try {
            task.setUpdateTime(new Date());
            int result = springScheduledTaskMapper.updateById(task);
            if (result > 0) {
                // 更新调度器中的任务
                dynamicTaskManager.updateTask(task);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新定时任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTask(String taskId) {
        try {
            SpringScheduledTask task = springScheduledTaskMapper.selectById(taskId);
            if (task == null) {
                log.warn("任务不存在: {}", taskId);
                return false;
            }

            // 先从调度器中移除
            dynamicTaskManager.removeTask(task.getTaskName());

            // 逻辑删除
            int result = springScheduledTaskMapper.deleteById(taskId);
            return result > 0;
        } catch (Exception e) {
            log.error("删除定时任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTaskByName(String taskName) {
        try {
            SpringScheduledTask task = springScheduledTaskMapper.selectByTaskName(taskName);
            if (task == null) {
                log.warn("任务不存在: {}", taskName);
                return false;
            }
            return deleteTask(task.getId());
        } catch (Exception e) {
            log.error("根据名称删除定时任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据名称删除定时任务失败", e);
        }
    }

    @Override
    public SpringScheduledTask getTaskById(String taskId) {
        return springScheduledTaskMapper.selectById(taskId);
    }

    @Override
    public SpringScheduledTask getTaskByName(String taskName) {
        return springScheduledTaskMapper.selectByTaskName(taskName);
    }

    @Override
    public List<SpringScheduledTask> getAllTasks() {
        return springScheduledTaskMapper.selectList();
    }

    @Override
    public List<SpringScheduledTask> getTasksByStatus(Integer status) {
        return springScheduledTaskMapper.selectByStatus(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startTask(String taskId) {
        try {
            SpringScheduledTask task = springScheduledTaskMapper.selectById(taskId);
            if (task == null) {
                log.warn("任务不存在: {}", taskId);
                return false;
            }

            // 更新状态为启用
            task.setStatus(0);
            task.setUpdateTime(new Date());
            int result = springScheduledTaskMapper.updateById(task);
            if (result > 0) {
                dynamicTaskManager.addTask(task);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("启动定时任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("启动定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stopTask(String taskId) {
        try {
            SpringScheduledTask task = springScheduledTaskMapper.selectById(taskId);
            if (task == null) {
                log.warn("任务不存在: {}", taskId);
                return false;
            }

            // 更新状态为停止
            task.setStatus(-1);
            task.setUpdateTime(new Date());
            int result = springScheduledTaskMapper.updateById(task);
            if (result > 0) {
                dynamicTaskManager.removeTask(task.getTaskName());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("停止定时任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("停止定时任务失败", e);
        }
    }

    @Override
    public boolean executeTask(String taskId) {
        try {
            SpringScheduledTask task = springScheduledTaskMapper.selectById(taskId);
            if (task == null) {
                log.warn("任务不存在: {}", taskId);
                return false;
            }
            dynamicTaskManager.executeTaskImmediately(task);
            return true;
        } catch (Exception e) {
            log.error("立即执行定时任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("立即执行定时任务失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskCron(String taskId, String cronExpression) {
        try {
            SpringScheduledTask task = springScheduledTaskMapper.selectById(taskId);
            if (task == null) {
                log.warn("任务不存在: {}", taskId);
                return false;
            }

            task.setCronExpression(cronExpression);
            task.setUpdateTime(new Date());
            int result = springScheduledTaskMapper.updateById(task);
            if (result > 0) {
                dynamicTaskManager.updateTaskCron(task.getTaskName(), cronExpression);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新任务调度时间失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新任务调度时间失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskMethod(String taskId, String taskClass, String taskMethod, String taskParameter) {
        try {
            SpringScheduledTask task = springScheduledTaskMapper.selectById(taskId);
            if (task == null) {
                log.warn("任务不存在: {}", taskId);
                return false;
            }

            task.setTaskClass(taskClass);
            task.setTaskMethod(taskMethod);
            task.setTaskParameter(taskParameter);
            task.setUpdateTime(new Date());
            int result = springScheduledTaskMapper.updateById(task);
            if (result > 0) {
                dynamicTaskManager.updateTaskMethod(task);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新任务执行方法失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新任务执行方法失败", e);
        }
    }

    @Override
    public void initAllTasks() {
        try {
            List<SpringScheduledTask> tasks = springScheduledTaskMapper.selectByStatus(0); // 只初始化启用的任务
            for (SpringScheduledTask task : tasks) {
                dynamicTaskManager.addTask(task);
            }
            log.info("初始化动态定时任务完成，共加载 {} 个任务", tasks.size());
        } catch (Exception e) {
            log.error("初始化动态定时任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("初始化动态定时任务失败", e);
        }
    }
}