package work.chncyl.base.global.task.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import work.chncyl.base.global.result.ApiResult;
import work.chncyl.base.global.task.SpringScheduledTask;
import work.chncyl.base.global.task.service.inter.ISpringScheduledTaskService;

import java.util.List;

/**
 * @Description: Spring动态定时任务控制器
 * @Author: chncyl
 * @Date: 2025-01-01
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/springScheduledTask")
public class SpringScheduledTaskController {

    private static final Logger log = LoggerFactory.getLogger(SpringScheduledTaskController.class);

    @Autowired
    private ISpringScheduledTaskService springScheduledTaskService;

    /**
     * 添加定时任务
     * @param task 任务实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public ApiResult<?> add(@RequestBody SpringScheduledTask task) {
        try {
            boolean result = springScheduledTaskService.addTask(task);
            if (result) {
                return ApiResult.success("添加定时任务成功");
            } else {
                return ApiResult.error500("添加定时任务失败");
            }
        } catch (Exception e) {
            log.error("添加定时任务失败", e);
            return ApiResult.error500("添加定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 更新定时任务
     * @param task 任务实体
     * @return 操作结果
     */
    @PutMapping("/update")
    public ApiResult<?> update(@RequestBody SpringScheduledTask task) {
        try {
            boolean result = springScheduledTaskService.updateTask(task);
            if (result) {
                return ApiResult.success("更新定时任务成功");
            } else {
                return ApiResult.error500("更新定时任务失败");
            }
        } catch (Exception e) {
            log.error("更新定时任务失败", e);
            return ApiResult.error500("更新定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 删除定时任务
     * @param id 任务ID
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public ApiResult<?> delete(@RequestParam(name = "id") String id) {
        try {
            boolean result = springScheduledTaskService.deleteTask(id);
            if (result) {
                return ApiResult.success("删除定时任务成功");
            } else {
                return ApiResult.error500("删除定时任务失败");
            }
        } catch (Exception e) {
            log.error("删除定时任务失败", e);
            return ApiResult.error500("删除定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 根据任务名称删除定时任务
     * @param taskName 任务名称
     * @return 操作结果
     */
    @DeleteMapping("/deleteByName")
    public ApiResult<?> deleteByName(@RequestParam(name = "taskName") String taskName) {
        try {
            boolean result = springScheduledTaskService.deleteTaskByName(taskName);
            if (result) {
                return ApiResult.success("删除定时任务成功");
            } else {
                return ApiResult.error500("删除定时任务失败");
            }
        } catch (Exception e) {
            log.error("删除定时任务失败", e);
            return ApiResult.error500("删除定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询定时任务
     * @param id 任务ID
     * @return 任务信息
     */
    @GetMapping("/getById")
    public ApiResult<SpringScheduledTask> getById(@RequestParam(name = "id") String id) {
        try {
            SpringScheduledTask task = springScheduledTaskService.getTaskById(id);
            if (task != null) {
                return ApiResult.success(task);
            } else {
                return ApiResult.error500("未找到对应的定时任务");
            }
        } catch (Exception e) {
            log.error("查询定时任务失败", e);
            return ApiResult.error500("查询定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 根据任务名称查询定时任务
     * @param taskName 任务名称
     * @return 任务信息
     */
    @GetMapping("/getByName")
    public ApiResult<SpringScheduledTask> getByName(@RequestParam(name = "taskName") String taskName) {
        try {
            SpringScheduledTask task = springScheduledTaskService.getTaskByName(taskName);
            if (task != null) {
                return ApiResult.success(task);
            } else {
                return ApiResult.error500("未找到对应的定时任务");
            }
        } catch (Exception e) {
            log.error("查询定时任务失败", e);
            return ApiResult.error500("查询定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有定时任务
     * @return 任务列表
     */
    @GetMapping("/list")
    public ApiResult<List<SpringScheduledTask>> list() {
        try {
            List<SpringScheduledTask> tasks = springScheduledTaskService.getAllTasks();
            return ApiResult.success(tasks);
        } catch (Exception e) {
            log.error("获取定时任务列表失败", e);
            return ApiResult.error500("获取定时任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据状态查询定时任务
     * @param status 状态
     * @return 任务列表
     */
    @GetMapping("/listByStatus")
    public ApiResult<List<SpringScheduledTask>> listByStatus(@RequestParam(name = "status") Integer status) {
        try {
            List<SpringScheduledTask> tasks = springScheduledTaskService.getTasksByStatus(status);
            return ApiResult.success(tasks);
        } catch (Exception e) {
            log.error("根据状态查询定时任务失败", e);
            return ApiResult.error500("根据状态查询定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 启动定时任务
     * @param id 任务ID
     * @return 操作结果
     */
    @PostMapping("/start")
    public ApiResult<?> start(@RequestParam(name = "id") String id) {
        try {
            boolean result = springScheduledTaskService.startTask(id);
            if (result) {
                return ApiResult.success("启动定时任务成功");
            } else {
                return ApiResult.error500("启动定时任务失败");
            }
        } catch (Exception e) {
            log.error("启动定时任务失败", e);
            return ApiResult.error500("启动定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 停止定时任务
     * @param id 任务ID
     * @return 操作结果
     */
    @PostMapping("/stop")
    public ApiResult<?> stop(@RequestParam(name = "id") String id) {
        try {
            boolean result = springScheduledTaskService.stopTask(id);
            if (result) {
                return ApiResult.success("停止定时任务成功");
            } else {
                return ApiResult.error500("停止定时任务失败");
            }
        } catch (Exception e) {
            log.error("停止定时任务失败", e);
            return ApiResult.error500("停止定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 立即执行定时任务
     * @param id 任务ID
     * @return 操作结果
     */
    @PostMapping("/execute")
    public ApiResult<?> execute(@RequestParam(name = "id") String id) {
        try {
            boolean result = springScheduledTaskService.executeTask(id);
            if (result) {
                return ApiResult.success("立即执行定时任务成功");
            } else {
                return ApiResult.error500("立即执行定时任务失败");
            }
        } catch (Exception e) {
            log.error("立即执行定时任务失败", e);
            return ApiResult.error500("立即执行定时任务失败: " + e.getMessage());
        }
    }

    /**
     * 更新任务调度时间
     * @param id 任务ID
     * @param cronExpression cron表达式
     * @return 操作结果
     */
    @PutMapping("/updateCron")
    public ApiResult<?> updateCron(@RequestParam(name = "id") String id,
                                   @RequestParam(name = "cronExpression") String cronExpression) {
        try {
            boolean result = springScheduledTaskService.updateTaskCron(id, cronExpression);
            if (result) {
                return ApiResult.success("更新任务调度时间成功");
            } else {
                return ApiResult.error500("更新任务调度时间失败");
            }
        } catch (Exception e) {
            log.error("更新任务调度时间失败", e);
            return ApiResult.error500("更新任务调度时间失败: " + e.getMessage());
        }
    }

    /**
     * 更新任务执行方法
     * @param id 任务ID
     * @param taskClass 任务类名
     * @param taskMethod 任务方法名
     * @param taskParameter 任务参数
     * @return 操作结果
     */
    @PutMapping("/updateMethod")
    public ApiResult<?> updateMethod(@RequestParam(name = "id") String id,
                                     @RequestParam(name = "taskClass") String taskClass,
                                     @RequestParam(name = "taskMethod") String taskMethod,
                                     @RequestParam(name = "taskParameter", required = false) String taskParameter) {
        try {
            boolean result = springScheduledTaskService.updateTaskMethod(id, taskClass, taskMethod, taskParameter);
            if (result) {
                return ApiResult.success("更新任务执行方法成功");
            } else {
                return ApiResult.error500("更新任务执行方法失败");
            }
        } catch (Exception e) {
            log.error("更新任务执行方法失败", e);
            return ApiResult.error500("更新任务执行方法失败: " + e.getMessage());
        }
    }
}