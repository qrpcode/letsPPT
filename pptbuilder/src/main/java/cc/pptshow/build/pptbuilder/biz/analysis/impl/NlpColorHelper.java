package cc.pptshow.build.pptbuilder.biz.analysis.impl;

import cc.pptshow.build.pptbuilder.biz.helper.BaiduImgHelper;
import cc.pptshow.build.pptbuilder.biz.helper.ColorExtractHelper;
import cc.pptshow.build.pptbuilder.domain.RGB;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * 通过自然文字判定应该使用的颜色
 */
@Slf4j
@Service
public class NlpColorHelper {

    @Resource
    private ColorExtractHelper colorExtractHelper;

    @Resource
    private BaiduImgHelper baiduImgHelper;

    @SneakyThrows
    public List<RGB> findColorByTitle(String title) {
        title = title + "素材";
        List<File> files = baiduImgHelper.downImage(title);
        List<RGB> colorSolution = Lists.newArrayList();
        for (File file : files) {
            if (Objects.isNull(file)) {
                return null;
            }
            colorSolution = colorExtractHelper.getColorSolution(ImageIO.read(file), 10);
            log.info("[推荐颜色] {}", colorSolution);
            if (!isHaveGrey(colorSolution) && !CollectionUtils.isEmpty(colorSolution)) {
                break;
            }
        }
        files.forEach(f -> f.delete());
        return colorSolution;
    }

    private boolean isHaveGrey(List<RGB> colors) {
        for (RGB color : colors) {
            if (color.getG() == color.getB() && color.getB() == color.getR()) {
                return true;
            }
            if (color.getR() > 200 && color.getG() > 200 && color.getB() > 200) {
                return true;
            }
        }
        return false;
    }

}
