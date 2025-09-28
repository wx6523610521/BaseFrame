package work.chncyl.service.permission.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import work.chncyl.base.global.security.utils.PermissionCacheUtil;
import work.chncyl.base.global.utils.SessionUtils;
import work.chncyl.service.permission.dto.input.*;
import work.chncyl.service.permission.entity.Role;
import work.chncyl.service.permission.mapper.RoleMapper;
import work.chncyl.service.permission.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    @Transactional
    public Integer addRole(RoleAddInput input) {
        if (input == null || StrUtil.isBlank(input.getMark())) {
            throw new RuntimeException("标记不能为空");
        }
        long count = count(new LambdaQueryWrapper<Role>()
                .eq(Role::getMark, input.getMark())
                .eq(Role::getIsDeleted, 0)
        );
        if (count > 0) {
            throw new RuntimeException("角色标识应当唯一");
        }

        Role role = new Role();
        role.setName(input.getName());
        role.setMark(input.getMark());
        role.setStatus(input.getStatus());
        role.setSort(input.getSort());
        role.setIsDefault(input.getIsDefault());
        role.setCreateTime(new java.util.Date());
        role.setCreator(SessionUtils.getSession().getUserId());
        // 插入数据 关联权限
        if (SqlHelper.retBool(baseMapper.insert(role))
                && input.getPermissions() != null
                && !input.getPermissions().isEmpty()
        ) {
            associate(new Associate(role.getId(), input.getPermissions()));
        }
        // 刷新角色缓存
        PermissionCacheUtil.RoleCache.refreshAll();
        return role.getId();
    }

    @Override
    public Boolean deleteRole(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        boolean result = update(new LambdaUpdateWrapper<Role>()
                .eq(Role::getId, id)
                .set(Role::getIsDeleted, 1)
                .set(Role::getUpdateTime, new Date())
                .set(Role::getUpdateUser, SessionUtils.getSession().getUserId())
        );
        if (result) {
            PermissionCacheUtil.RoleCache.remove(id);
            PermissionCacheUtil.PermissionCache.remove(String.valueOf(id));
        }
        return result;
    }

    @Override
    public Boolean disassociate(Associate associate) {
        if (associate == null
                || associate.getPrincipalId() == null
        ) {
            throw new IllegalArgumentException("参数缺失");
        }

        boolean result = SqlHelper.retBool(baseMapper.disassociate(associate));
        if (result) {
            PermissionCacheUtil.PermissionCache.refreshAll();
        }
        return result;
    }

    @Override
    public Boolean disassociateAll(Integer roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        Associate associate = new Associate();
        associate.setPrincipalId(roleId);
        boolean result = SqlHelper.retBool(baseMapper.disassociate(associate));
        if (result) {
            PermissionCacheUtil.PermissionCache.refreshByRoleId(roleId);
        }
        return result;
    }

    @Override
    public Boolean associate(Associate associate) {
        if (associate == null
                || associate.getPrincipalId() == null
                || associate.getRelevanceId() == null
                || associate.getRelevanceId().isEmpty()
        ) {
            throw new IllegalArgumentException("参数缺失");
        }
        boolean result = SqlHelper.retBool(baseMapper.associate(associate));
        if (result) {
            PermissionCacheUtil.PermissionCache.refreshByRoleId(associate.getPrincipalId());
        }
        return result;
    }

    @Override
    public Boolean changeStatus(ChangeStatus input) {
        boolean result = update(new LambdaUpdateWrapper<Role>()
                .in(Role::getId, input.getId())
                .set(Role::getStatus, input.getStatus())
                .set(Role::getUpdateTime, new Date())
                .set(Role::getUpdateUser, SessionUtils.getSession().getUserId())
        );
        if (result) {
            PermissionCacheUtil.RoleCache.refreshAll();
        }
        return result;
    }

    @Override
    public Boolean editRole(RoleEditInput input) {
        if (StrUtil.isBlank(input.getMark())) {
            throw new RuntimeException("标记不能为空");
        }
        long count = count(new LambdaQueryWrapper<Role>()
                .eq(Role::getMark, input.getMark())
                .eq(Role::getIsDeleted, 0)
                .ne(Role::getId, input.getId())
        );
        if (count > 0) {
            throw new RuntimeException("角色标识应当唯一");
        }

        Role role = new Role();
        role.setId(input.getId());
        role.setName(input.getName());
        role.setMark(input.getMark());
        role.setStatus(input.getStatus());
        role.setSort(input.getSort());
        role.setIsDefault(input.getIsDefault());
        role.setCreateTime(new java.util.Date());
        role.setCreator(SessionUtils.getSession().getUserId());
        // 插入数据 关联权限
        if (SqlHelper.retBool(baseMapper.updateById(role))
                && input.getPermissions() != null
                && !input.getPermissions().isEmpty()
        ) {
            disassociateAll(role.getId());
            associate(new Associate(role.getId(), input.getPermissions()));
        }
        // 刷新角色缓存和权限缓存
        PermissionCacheUtil.RoleCache.refreshById(role.getId());
        PermissionCacheUtil.PermissionCache.refreshByRoleId(role.getId());
        return true;
    }

    @Override
    public IPage<Role> roleList(RoleSearchInput input) {
        IPage<Role> page = new Page<>(input.getPageIndex(), input.getSize());
        return baseMapper.page(page, input);
    }

    @Override
    public Boolean associateRole(Associate associate) {
        if (associate == null
                || associate.getPrincipalId() == null
                || associate.getRelevanceId() == null
                || associate.getRelevanceId().isEmpty()
        ) {
            throw new IllegalArgumentException("参数缺失");
        }

        return SqlHelper.retBool(baseMapper.associateRole(associate));
    }

}
