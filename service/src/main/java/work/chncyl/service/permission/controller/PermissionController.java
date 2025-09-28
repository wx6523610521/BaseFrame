package work.chncyl.service.permission.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import work.chncyl.base.global.pojo.EntityDtoInt;
import work.chncyl.base.global.result.ApiResult;
import work.chncyl.service.permission.dto.input.*;
import work.chncyl.service.permission.entity.Permissions;
import work.chncyl.service.permission.entity.Role;
import work.chncyl.service.permission.service.PermissionsService;
import work.chncyl.service.permission.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission")
@Api(tags = "权限")
@RequiredArgsConstructor
@RequiresRoles("admin")
public class PermissionController {
    private final RoleService roleService;
    private final PermissionsService permissionService;

    @PostMapping("/addRole")
    @ApiOperation(value = "新增角色")
    public ApiResult<Integer> addRole(@RequestBody RoleAddInput input) {
        return ApiResult.success(roleService.addRole(input));
    }

    @PostMapping("/addPermission")
    @ApiOperation(value = "新增权限")
    public ApiResult<Integer> addPermission(@RequestBody PermissionAddInput input) {
        return ApiResult.success(permissionService.addPermission(input));
    }

    @PostMapping("/deleteRole")
    @ApiOperation(value = "删除角色")
    public ApiResult<Boolean> deleteRole(@RequestBody EntityDtoInt id) {
        return ApiResult.success(roleService.deleteRole(id.getId()));
    }

    @PostMapping("/deletePermission")
    @ApiOperation(value = "删除权限")
    public ApiResult<Boolean> deletePermission(@RequestBody EntityDtoInt id) {
        return ApiResult.success(permissionService.deletePermission(id.getId()));
    }

    @PostMapping("/disassociate")
    @ApiOperation(value = "删除权限关联")
    public ApiResult<Boolean> disassociate(@RequestBody Associate associate) {
        return ApiResult.success(roleService.disassociate(associate));
    }

    @PostMapping("/associate")
    @ApiOperation(value = "添加权限关联")
    public ApiResult<Boolean> associate(@RequestBody Associate associate) {
        return ApiResult.success(roleService.associate(associate));
    }

    @PostMapping("/changeRoleStatus")
    @ApiOperation(value = "修改角色状态")
    public ApiResult<Boolean> changeRoleStatus(@RequestBody ChangeStatus input) {
        return ApiResult.success(roleService.changeStatus(input));
    }

    @PostMapping("/changePermissionStatus")
    @ApiOperation(value = "修改角色状态")
    public ApiResult<Boolean> changePermissionStatus(@RequestBody ChangeStatus input) {
        return ApiResult.success(permissionService.changeStatus(input));
    }

    @PostMapping("/editRole")
    @ApiOperation(value = "修改角色")
    public ApiResult<Boolean> editRole(@RequestBody RoleEditInput input) {
        return ApiResult.success(roleService.editRole(input));
    }

    @PostMapping("/editPermission")
    @ApiOperation(value = "修改权限")
    public ApiResult<Boolean> editPermission(@RequestBody PermissionEditInput input) {
        return ApiResult.success(permissionService.editPermission(input));
    }

    @GetMapping("/roleList")
    @ApiOperation(value = "获取角色列表")
    public ApiResult<IPage<Role>> roleList(RoleSearchInput input) {
        return ApiResult.success(roleService.roleList(input));
    }

    @GetMapping("/permissionList")
    @ApiOperation(value = "获取权限列表")
    public ApiResult<IPage<Permissions>> permissionList(PermissionSearchInput input) {
        return ApiResult.success(permissionService.permissionList(input));
    }

    @PostMapping("/associateRole")
    @ApiOperation(value = "添加角色关联至用户")
    public ApiResult<Boolean> associateRole(@RequestBody Associate associate) {
        return ApiResult.success(roleService.associateRole(associate));
    }
}
