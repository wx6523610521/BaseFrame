package work.chncyl.base.global.utils;

import cn.hutool.core.util.StrUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 * html文本处理工具类
 *
 * @author chncyl
 */
public class HtmlUtils {
    /**
     * 截断html
     *
     * @param html  原文
     * @param scale 截取比例 0.5、0.3
     */
    public static String substringHtml(String html, double scale) {
        if (scale >= 1) {
            return html;
        }
        if (scale <= 0 || StrUtil.isBlank(html)) {
            return "";
        }
        Document doc = parse(html);
        // 计算保留文字数量
        int length = (int) (doc.body().text().length() * scale);

        return doSub(doc, length);
    }

    /**
     * 截断html
     *
     * @param html   原文
     * @param length 字数
     */
    public static String substringHtml(String html, int length) {
        if (length <= 0 || StrUtil.isBlank(html)) {
            return "";
        }
        Document doc = parse(html);

        return doSub(doc, length);
    }

    private static String doSub(Document doc, int length) {
        StringBuilder result = new StringBuilder();
        int currentLength = 0;

        for (Element element : doc.body().children()) {
            currentLength += appendElementContent(element, result, length - currentLength);
            if (currentLength >= length) {
                break;
            }
        }
        // 补全未闭合的标签
        Document resultDoc = parse(result.toString());
        Elements openTags = resultDoc.select("*:not(:has(*))");
        for (int i = openTags.size() - 1; i >= 0; i--) {
            Element tag = openTags.get(i);
            if (!tag.isBlock() && !tag.toString().endsWith("/>")) {
                result.append("</").append(tag.tagName()).append(">");
            }
        }

        return result.toString();
    }

    /**
     * 从html中选择元素
     *
     * @param html        html原文
     * @param cssSelector 选择器(使用css语法，支持伪选择器)
     */
    public static Elements substringHtml(String html, String cssSelector) {
        if (StrUtil.isBlank(html)) {
            return new Elements();
        }
        Document doc = parse(html);
        if (StrUtil.isBlank(cssSelector) || "*".equals(cssSelector)) {
            return doc.body().children();
        }
        return doc.select(cssSelector);
    }
    
    public static Document parse(String html) {
        return Jsoup.parse(html);
    }


    private static int appendElementContent(Element element, StringBuilder result, int remainingLength) {
        int lengthAdded = 0;
        // 获取标签名
        String tagName = element.tagName();
        // 获取所有属性
        Attributes attributes = element.attributes();
        // 构建标签字符串
        result.append("<").append(tagName);
        for (Attribute attribute : attributes) {
            result.append(" ").append(attribute.getKey()).append("=\"").append(attribute.getValue()).append("\"");
        }
        result.append(">");
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                String text = ((TextNode) node).text();
                if (lengthAdded + text.length() > remainingLength) {
                    text = text.substring(0, remainingLength - lengthAdded);
                }
                result.append(text);
                lengthAdded += text.length();
                if (lengthAdded >= remainingLength) {
                    break;
                }
            } else if (node instanceof Element) {
                Element childElement = (Element) node;
                lengthAdded += appendElementContent(childElement, result, remainingLength - lengthAdded);
                if (lengthAdded >= remainingLength) {
                    break;
                }
            }
        }
        result.append("</").append(element.tagName()).append(">");
        return lengthAdded;
    }
}
