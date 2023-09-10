package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.domain.TextCount;
import cc.pptshow.ppt.constant.Constant;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.util.Strings.EMPTY;

/**
 * 文案辅助类
 */
public class TextUtil {

    public static int widthToMaxLength(double width, int fontSize) {
        return (int) ((width * Constant.MULTIPLE_CM) / (fontSize * Constant.MULTIPLE_4 * 1.3));
    }

    public static String toTitle(int length) {
        if (length == 6) {
            return "工作内容汇报";
        } else if (length == 7) {
            return "上半年工作汇报";
        } else if (length == 8) {
            return "简约商务内容汇报";
        } else if (length == 9) {
            return "简约商务风工作汇报";
        } else if (length == 10) {
            return "简约商务工作内容汇报";
        } else if (length == 11) {
            return "简约商务增长率概况汇报";
        } else if (length == 12) {
            return "简约商务风格工作内容汇报";
        } else if (length == 13) {
            return "简约商务风格增长率概况汇报";
        } else if (length == 14) {
            return "简约商务风格季度工作内容汇报";
        } else if (length == 15) {
            return "简约商务风格季度增长率概况汇报";
        }
        System.out.println(length);
        return "123456";
    }

    public static String topSmallText() {
        return "企业介绍商务PPT模板";
    }

    public static String topBigKeyWord() {
        return "企业工坊";
    }

    public static String topBigKeyWordAfterYear() {
        return "企业工坊";
    }

    public static String toDescribeLorem() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et " +
                "dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco.";
    }

    public static String toDescribeKeyword() {
        return "拉起差距 | 明确赛道 | 风险明确 | 税务筹划";
    }

    public static String getSubString(String text, String left, String right) {
        String result = "";
        int zLen;
        if (left == null || left.isEmpty()) {
            zLen = 0;
        } else {
            zLen = text.indexOf(left);
            if (zLen > -1) {
                zLen += left.length();
            } else {
                zLen = 0;
            }
        }
        int yLen = text.indexOf(right, zLen);
        if (yLen < 0 || right.isEmpty()) {
            yLen = text.length();
        }
        result = text.substring(zLen, yLen);
        return result;
    }

    /**
     * 是不是大标题文本
     * 大标题：“目录” “谢谢观看” 等
     */
    public static boolean isBigTitle(String text) {
        if (Strings.isEmpty(text)) {
            return false;
        }
        text = text.replace(" ", "");
        return "目录".equals(text) || "contents".equals(text.toLowerCase(Locale.ROOT));
    }

    /**
     * 字符串是否包含中文
     */
    public static boolean isContainChinese(String str) {

        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isAllNumber(String text) {
        return Strings.isEmpty(text.replace("0", EMPTY)
                .replace("1", EMPTY)
                .replace("2", EMPTY)
                .replace("3", EMPTY)
                .replace("4", EMPTY)
                .replace("5", EMPTY)
                .replace("6", EMPTY)
                .replace("7", EMPTY)
                .replace("8", EMPTY)
                .replace("9", EMPTY));
    }

    public static TextCount findTextCount(String str) {
        String E1 = "[\u4e00-\u9fa5]";// 中文
        String E2 = "[a-zA-Z]";// 英文
        String E3 = "[0-9]";// 数字
        int chineseCount = 0;
        int englishCount = 0;
        int numberCount = 0;
        String temp;
        for (int i = 0; i < str.length(); i++)
        {
            temp = String.valueOf(str.charAt(i));
            if (temp.matches(E1))
            {
                chineseCount++;
            }
            if (temp.matches(E2))
            {
                englishCount++;
            }
            if (temp.matches(E3))
            {
                numberCount++;
            }
        }

        return new TextCount(chineseCount, englishCount, numberCount,
                (str.length() - (chineseCount + englishCount + numberCount)));
    }

    public static String replaceText(String oldText, String needReplaceText, String replaceText) {
        if (Strings.isEmpty(oldText)) {
            return oldText;
        }
        byte[] oldTextBytes = oldText.getBytes();
        byte[] needReplaceTextBytes = needReplaceText.getBytes();
        byte[] replaceTextBytes = replaceText.getBytes();
        int j = 0;
        for (int i = 0; i < oldTextBytes.length; i++) {
            if (needReplaceTextBytes.length < i + 1) {
                break;
            }
            if (oldTextBytes[i] == needReplaceTextBytes[j]) {
                if (replaceTextBytes.length > j) {
                    oldTextBytes[i] = replaceTextBytes[j];
                    j++;
                } else {
                    oldTextBytes[i] = ' ';
                }
            }
        }
        return new String(oldTextBytes);
    }



    public static String getChinese(String source) {
        char[] chars = source.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char aChar : chars) {
            int length = String.valueOf(aChar).getBytes().length;
            if (length == 3) {
                stringBuilder.append(aChar);
            }
        }

        return stringBuilder.toString();
    }

    //通过ASCII表完成字符的匹配
    public static String getAlphabet(String source) {
        char[] chars = source.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char aChar : chars) {
            if (aChar > 127) {
                stringBuilder.append(aChar);
            }
        }
        return stringBuilder.toString();
    }

    public static int getChineseStart(String allText) {
        String chinese = TextUtil.getChinese(allText);
        if (Strings.isEmpty(chinese)) {
            return 0;
        }
        return allText.indexOf(chinese.substring(0, 1));
    }


}

