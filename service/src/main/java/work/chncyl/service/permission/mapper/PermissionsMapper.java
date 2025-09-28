package work.chncyl.service.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import work.chncyl.service.permission.dto.input.PermissionSearchInput;
import work.chncyl.service.permission.entity.Permissions;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionsMapper extends BaseMapper<Permissions> {
    List<String> selectPermissionByRole(@Param("roleIds") List<String> roleIds);

    IPage<Permissions> selectPermissionsPage(@Param("page") IPage<Permissions> page, @Param("input") PermissionSearchInput input);
}
