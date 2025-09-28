package work.chncyl.base.global.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口文档配置
 */
@Configuration
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerConfig {
    /**
     * 是否启用
     */
    private String enabled;

    private String title;

    private String description;

    private String author;

    private String version;

    private List<String> security = new ArrayList<>();
}