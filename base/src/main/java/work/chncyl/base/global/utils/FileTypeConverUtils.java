package work.chncyl.base.global.utils;

import cn.hutool.core.util.StrUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件转换工具类
 *
 * @author chncyl
 */
public class FileTypeConverUtils {
    /**
     * 文件转换成字符串
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String fileToString(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件转换成字符串
     *
     * @param file 文件
     * @return 文件内容
     */
    public static String fileToString(File file) {
        if (file == null) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * markdown转换成pdf
     *
     * @param markdown markdown内容
     * @param outputFilePath 输出文件路径包含名称
     * @throws IOException
     * @throws DocumentException
     */
    public static void markdownToPdf(String markdown, String outputFilePath) throws IOException, DocumentException {
        if (StrUtil.isBlank(outputFilePath)) {
            throw new RuntimeException("参数缺失");
        }
        if(!outputFilePath.endsWith(".pdf")){
            throw new RuntimeException("参数错误");
        }
        guaranteeNewDocument(outputFilePath);
        OutputStream os = Files.newOutputStream(Paths.get(outputFilePath));
        markdownToPdf(markdown, os);
    }

    /**
     * markdown转换成pdf
     *
     * @param markdown markdown内容
     * @param os 输出流
     * @throws IOException
     * @throws DocumentException
     */
    public static void markdownToPdf(String markdown, OutputStream os) throws IOException, DocumentException {
        String html = markdownToHtml(markdown);
        htmlToPdf(html, os);
    }

    /**
     * markdown转换成html
     *
     * @param markdown markdown内容
     * @return html内容
     */
    public static String markdownToHtml(String markdown) {
        // markdown解析
        Parser parser = Parser.builder().build();
        // html渲染
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(markdown));
    }

    /**
     * html转换成pdf
     *
     * @param html html内容
     * @param os 输出流
     * @throws IOException
     * @throws DocumentException
     */
    public static void htmlToPdf(String html, OutputStream os) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
            document.open();
            document.addCreationDate();

            // 构建完整的HTML模板,确保中文正确显示
            String htmlTemplate =
                    "<!DOCTYPE html><html><head>" +
                            "<meta charset='UTF-8'/>" +
                            "<style type='text/css'>" +
                            "body { font-family: SimSun; }" +
                            "* { font-family: SimSun; }" +
                            "</style>" +
                            "</head><body>" + html + "</body></html>";

            // 创建自定义的AsianFontProvider
            class AsianFontProvider extends XMLWorkerFontProvider {
                public AsianFontProvider() throws IOException {
                    super(ResourceFileUtils.getFileFromResource(File.separator + "fonts" + File.separator).getAbsolutePath());
                }

                @Override
                public Font getFont(String fontName, String encoding, boolean embedded, float size, int style, com.itextpdf.text.BaseColor color) {
                    try {
                        // 尝试使用 itext-asian自带的宋体
                        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                        return new Font(baseFont, size, style, color);
                    } catch (Exception e) {
                        try {
                            // 如果上面的字体不可用，尝试使用系统安装的宋体
//                            String fontPath = "C:/Windows/Fonts/simsun.ttc";
                            String fontPath = ResourceFileUtils.getFileFromResource(File.separator + "fonts" + File.separator).getAbsolutePath();
                            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                            return new Font(baseFont, size, style, color);
                        } catch (Exception ex) {
                            return super.getFont(fontName, encoding, embedded, size, style, color);
                        }
                    }
                }
            }
            // css处理器
            CSSResolver cssResolver = new StyleAttrCSSResolver();
            CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(
                    "* { font-family: SimSun; }".getBytes(StandardCharsets.UTF_8)));
            cssResolver.addCss(cssFile);

            XMLWorkerFontProvider fontProvider = new AsianFontProvider();
            CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

            Pipeline<?> pipeline = new CssResolverPipeline(cssResolver,
                    new HtmlPipeline(htmlContext,
                            new PdfWriterPipeline(document, pdfWriter)));

            XMLWorker worker = new XMLWorker(pipeline, true);
            XMLParser xmlParser = new XMLParser(worker, StandardCharsets.UTF_8);

            xmlParser.parse(new ByteArrayInputStream(htmlTemplate.getBytes(StandardCharsets.UTF_8)));
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    /**
     * 确保文件存在且是新文件
     *
     * @param filePath 文件路径
     */
    private static void guaranteeNewDocument(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        guaranteeDocumentExist(filePath);
    }

    /**
     * 确保文件存在
     *
     * @param filePath 文件路径
     */
    private static void guaranteeDocumentExist(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return;
        }
        File file = new File(filePath);
        // 查看文件上级文件夹，不存在则创建
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
