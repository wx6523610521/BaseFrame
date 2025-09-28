package work.chncyl.system.authentication.dto;

import lombok.Data;

/**
 * 用户信息输入DTO
 */
@Data
public class UserInfoInput {
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 单位
     */
    private String company;
    
}
