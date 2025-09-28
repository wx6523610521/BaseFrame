package work.chncyl.service.permission.dto.input;

import lombok.Data;

import java.util.List;

@Data
public class ChangeStatus {
    private List<Integer> id;

    private Integer status;
}
