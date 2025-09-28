package work.chncyl.base.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.chncyl.base.global.security.dto.RoleInfo;
import work.chncyl.base.global.security.dto.RolePermissionsSearch;
import work.chncyl.base.global.security.dto.RoleSearchDto;
import work.chncyl.base.global.service.dto.Dictionary;
import work.chncyl.base.global.service.dto.DictionarySearchInfo;
import work.chncyl.base.global.service.mapper.GlobalMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GlobalService {
    private final GlobalMapper globalMapper;

    public List<Dictionary> searchDictionarys(DictionarySearchInfo info) {
        return globalMapper.searchDictionarys(info);
    }


    public List<RoleInfo> getRoles(RoleSearchDto dto) {
        if (dto == null) {
            dto = RoleSearchDto.builder().build();
        }
        return globalMapper.getRoles(dto);
    }

    public Map<String, List<String>> getRolePermissions(RolePermissionsSearch dto) {
        List<Map<String, String>> list = globalMapper.getRolePermissions(dto);
        Map<String, List<String>> result = new HashMap<>();
        if (list == null || list.isEmpty()) {
            return result;
        }
        list.forEach(map -> {
            String rId = String.valueOf(map.get("roleId"));
            List<String> roleId = result.get(rId);
            if (roleId == null) {
                roleId = new ArrayList<>();
            }
            roleId.add(map.get("permissionName"));
            result.put(rId, roleId);
        });

        return result;
    }
}
