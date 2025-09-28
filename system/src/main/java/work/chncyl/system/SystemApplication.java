package work.chncyl.system;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication(scanBasePackages = {"work.chncyl"})
@EnableAsync
@EnableScheduling
//@Import(ShiroConfig.class)
@MapperScan(basePackages = {"work.chncyl.**.mapper"})
@Slf4j
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SystemApplication.class);
        Environment env = app.run(args).getEnvironment();
        app.setBannerMode(Banner.Mode.CONSOLE);
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {

        if (Boolean.TRUE.equals(env.getProperty("knife4j.enable", Boolean.class))) {
            String protocol = "http";
            if (env.getProperty("server.ssl.key-store") != null) {
                protocol = "https";
            }
            String serverPort = env.getProperty("server.port");
            String contextPath = env.getProperty("server.servlet.context-path");
            if (StringUtils.isBlank(contextPath)) {
                contextPath = "/doc.html";
            } else {
                contextPath = contextPath + "/doc.html";
            }
            String hostAddress = "localhost";
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.warn("The host name could not be determined, using `localhost` as fallback");
            }
            log.info(
                    "----------------------------------------------------------\n" +
                            " \t应用程序“{}”正在运行中......\n" +
                            " \t接口文档访问 URL:\n" +
                            "\t本地: \t{}://localhost:{}{}\n" +
                            " \t外部: \t{}://{}:{}{}\n" +
                            " \t配置文件: \t{}\n" +
                            " ----------------------------------------------------------",
                    env.getProperty("spring.application.name"),
                    protocol,
                    serverPort,
                    contextPath,
                    protocol,
                    hostAddress,
                    serverPort,
                    contextPath,
                    env.getActiveProfiles());
        }
    }

    /**
     * 将控制器返回的逻辑视图名称解析为实际的视图资源路径（ModuleAndView转为真实视图）
     */
    @Bean
    @Order
    public InternalResourceViewResolver internalResourceViewResolver(){
        return new InternalResourceViewResolver();
    }
}
