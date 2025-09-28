package work.chncyl.service.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import work.chncyl.service.permission.dto.input.Associate;
import work.chncyl.service.permission.dto.input.RoleSearchInput;
import work.chncyl.service.permission.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    Integer disassociate(@Param("associate") Associate associate);

    Integer associate(@Param("associate") Associate associate);

    IPage<Role> page(@Param("page") IPage<Role> page, @Param("input") RoleSearchInput input);

    List<Role> getDefaultRole();

    Integer associateRole(@Param("associate") Associate associate);

}