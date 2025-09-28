package work.chncyl.base.global.service.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import work.chncyl.base.global.security.dto.RoleInfo;
import work.chncyl.base.global.security.dto.RolePermissionsSearch;
import work.chncyl.base.global.security.dto.RoleSearchDto;
import work.chncyl.base.global.service.dto.Dictionary;
import work.chncyl.base.global.service.dto.DictionarySearchInfo;

import java.util.List;
import java.util.Map;

@Mapper
public interface GlobalMapper {
    List<Dictionary> searchDictionarys(@Param("info") DictionarySearchInfo info);

    List<Map<String, String>> getRolePermissions(@Param("dto") RolePermissionsSearch dto);

    List<RoleInfo> getRoles(@Param("dto") RoleSearchDto dto);
}
