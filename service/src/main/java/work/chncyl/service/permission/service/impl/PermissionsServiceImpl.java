package work.chncyl.service.permission.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.chncyl.base.global.security.utils.PermissionCacheUtil;
import work.chncyl.base.global.utils.SessionUtils;
import work.chncyl.service.permission.dto.input.ChangeStatus;
import work.chncyl.service.permission.dto.input.PermissionAddInput;
import work.chncyl.service.permission.dto.input.PermissionEditInput;
import work.chncyl.service.permission.dto.input.PermissionSearchInput;
import work.chncyl.service.permission.entity.Permissions;
import work.chncyl.service.permission.mapper.PermissionsMapper;
import work.chncyl.service.permission.service.PermissionsService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PermissionsServiceImpl extends ServiceImpl<PermissionsMapper, Permissions> implements PermissionsService {

    @Override
    public Integer addPermission(PermissionAddInput input) {
        if (StrUtil.isBlank(input.getPermissionName())) {
            throw new RuntimeException("标记不能为空");
        }
        long count = count(new LambdaQueryWrapper<Permissions>()
                .eq(Permissions::getPermissionName, input.getPermissionName())
                .eq(Permissions::getIsDeleted, 0)
        );
        if (count > 0) {
            throw new RuntimeException("权限标识应当唯一");
        }
        // 插入数据
        Permissions permissions = new Permissions();
        permissions.setDisplayName(input.getDisplayName());
        permissions.setPermissionName(input.getPermissionName());
        permissions.setUrl(input.getUrl());
        permissions.setLevel(input.getLevel());
        permissions.setSort(input.getSort());
        permissions.setType(input.getType());
        permissions.setCreateTime(new Date());
        permissions.setCreator(SessionUtils.getSession().getUserId());
        baseMapper.insert(permissions);
        return permissions.getId();
    }

    @Override
    public Boolean deletePermission(Integer id) {
        boolean result = update(new LambdaUpdateWrapper<Permissions>()
                .eq(Permissions::getId, id)
                .set(Permissions::getIsDeleted, 1)
                .set(Permissions::getUpdateTime, new Date())
                .set(Permissions::getUpdateUser, SessionUtils.getSession().getUserId())
        );
        if (result) {
            // 刷新权限缓存
            PermissionCacheUtil.PermissionCache.refreshAll();
        }
        return result;
    }

    @Override
    public Boolean changeStatus(ChangeStatus input) {
        boolean result = update(new LambdaUpdateWrapper<Permissions>()
                .in(Permissions::getId, input.getId())
                .set(Permissions::getStatus, input.getStatus())
                .set(Permissions::getUpdateTime, new Date())
                .set(Permissions::getUpdateUser, SessionUtils.getSession().getUserId())
        );
        if (result) {
            // 刷新权限缓存
            PermissionCacheUtil.PermissionCache.refreshAll();
        }
        return result;
    }

    @Override
    public Boolean editPermission(PermissionEditInput input) {
        if (StrUtil.isBlank(input.getPermissionName())) {
            throw new RuntimeException("标记不能为空");
        }
        long count = count(new LambdaQueryWrapper<Permissions>()
                .eq(Permissions::getPermissionName, input.getPermissionName())
                .eq(Permissions::getIsDeleted, 0)
                .ne(Permissions::getId, input.getId())
        );
        if (count > 0) {
            throw new RuntimeException("权限标识应当唯一");
        }
        // 更新数据
        boolean result = update(new LambdaUpdateWrapper<Permissions>()
                .eq(Permissions::getId, input.getId())
                .set(Permissions::getDisplayName, input.getDisplayName())
                .set(Permissions::getPermissionName, input.getPermissionName())
                .set(Permissions::getUrl, input.getUrl())
                .set(Permissions::getLevel, input.getLevel())
                .set(Permissions::getSort, input.getSort())
                .set(Permissions::getType, input.getType())
                .set(Permissions::getUpdateTime, new Date())
                .set(Permissions::getUpdateUser, SessionUtils.getSession().getUserId())
        );
        if (result) {
            // 刷新权限缓存
            PermissionCacheUtil.PermissionCache.refreshAll();
        }
        return result;
    }

    @Override
    public IPage<Permissions> permissionList(PermissionSearchInput input) {
        IPage<Permissions> page = new Page<>(input.getPageIndex(), input.getSize());
        return baseMapper.selectPermissionsPage(page, input);
    }
}
