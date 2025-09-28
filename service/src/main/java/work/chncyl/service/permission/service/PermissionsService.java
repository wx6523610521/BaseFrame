package work.chncyl.service.permission.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import work.chncyl.service.permission.dto.input.ChangeStatus;
import work.chncyl.service.permission.dto.input.PermissionAddInput;
import work.chncyl.service.permission.dto.input.PermissionEditInput;
import work.chncyl.service.permission.dto.input.PermissionSearchInput;
import work.chncyl.service.permission.entity.Permissions;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PermissionsService extends IService<Permissions>{

    Integer addPermission(PermissionAddInput input);

    Boolean deletePermission(Integer id);

    Boolean changeStatus(ChangeStatus input);

    Boolean editPermission(PermissionEditInput input);

    IPage<Permissions> permissionList(PermissionSearchInput input);
}
