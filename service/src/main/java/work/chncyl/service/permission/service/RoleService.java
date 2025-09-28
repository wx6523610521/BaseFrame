package work.chncyl.service.permission.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import work.chncyl.service.permission.dto.input.*;
import work.chncyl.service.permission.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role>{


    Integer addRole(RoleAddInput input);

    Boolean deleteRole(Integer id);

    Boolean disassociate(Associate associate);

    Boolean disassociateAll(Integer id);

    Boolean associate(Associate associate);

    Boolean changeStatus(ChangeStatus input);

    Boolean editRole(RoleEditInput input);

    IPage<Role> roleList(RoleSearchInput input);

    Boolean associateRole(Associate associate);
}
