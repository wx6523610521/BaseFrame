package work.chncyl.base.global.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class TempFileCleaner {
    
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void cleanTempFiles() {
        File tempDir = new File("temp");
        if (tempDir.exists() && tempDir.isDirectory()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 删除24小时前的文件
                    if (System.currentTimeMillis() - file.lastModified() > 24 * 60 * 60 * 1000) {
                        file.delete();
                    }
                }
            }
        }
    }
}