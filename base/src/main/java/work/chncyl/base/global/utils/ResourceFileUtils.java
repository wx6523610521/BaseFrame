package work.chncyl.base.global.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * <p>Description: 资源文件工具类</p>
 *
 * @author chncyl
 */
public class ResourceFileUtils {

    /**
     * 根据路径从项目resources目录下获取对应文件并返回File对象
     * @param relativePath 文件相对路径，如 /data/1.txt 或 data/1.txt
     * @return 文件的File对象
     * @throws IOException 如果文件不存在或无法读取
     */
    public static File getFileFromResource(String relativePath) throws IOException {
        Resource resource = new ClassPathResource(relativePath);
        return resource.getFile();
    }

    /**
     * 根据路径返回文件的绝对路径
     * @param relativePath 文件相对路径，如 /data/1.txt 或 data/1.txt
     * @return 文件的绝对路径
     * @throws IOException 如果文件不存在或无法读取
     */
    public static String getAbsolutePathFromResource(String relativePath) throws IOException {
        Resource resource = new ClassPathResource(relativePath);
        return resource.getFile().getAbsolutePath();
    }

    public static void main(String[] args) {
        try {
            // 示例路径
            String path1 = "/fonts/simsum.ttf";
            String path2 = "fonts/simsum.ttf";

            // 获取File对象
            File file1 = getFileFromResource(path1);
            File file2 = getFileFromResource(path2);

            // 输出文件的File对象
            System.out.println("File object for " + path1 + ": " + file1);
            System.out.println("File object for " + path2 + ": " + file2);

            // 获取文件的绝对路径
            String absolutePath1 = getAbsolutePathFromResource(path1);
            String absolutePath2 = getAbsolutePathFromResource(path2);

            // 输出文件的绝对路径
            System.out.println("Absolute path for " + path1 + ": " + absolutePath1);
            System.out.println("Absolute path for " + path2 + ": " + absolutePath2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
