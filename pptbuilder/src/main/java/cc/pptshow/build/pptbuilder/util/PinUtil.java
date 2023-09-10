package cc.pptshow.build.pptbuilder.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Slf4j
public class PinUtil {


    public static String getPinyin(String china) {
        HanyuPinyinOutputFormat formart = new HanyuPinyinOutputFormat();
        formart.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formart.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        formart.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] arrays = china.trim().toCharArray();
        StringBuilder result = new StringBuilder();
        try {
            for (char ti : arrays) {
                if (Character.toString(ti).matches("[\\u4e00-\\u9fa5]")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(ti, formart);
                    if (Objects.nonNull(temp)) {
                        result.append(temp[0]);
                    }
                } else {
                    result.append(ti);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            log.error("拼音转换：", e);
        }

        String pin = result.toString().replaceAll("[^A-Za-z0-9]", "");
        return StringUtils.isEmpty(pin) ? "0" : pin;
    }

}
