package work.chncyl.base.global.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class RoleSearchDto {
    private List<Integer> roleIds;

    private Boolean defaultRole;
}
