package cc.pptshow.build.pptbuilder.biz.design.help;

import cc.pptshow.build.pptbuilder.bean.ImgInfo;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.build.pptbuilder.service.NullPicInfoService;
import cc.pptshow.build.pptbuilder.util.PathUtil;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cn.hutool.core.img.Img;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 图片筛选的辅助类
 */
@Service
public class ImgHelper {

    @Resource
    private ImgInfoService imgInfoService;

    @Resource
    private FromHelper fromHelper;

    @Resource
    private NullPicInfoService nullPicInfoService;

    public String findRandImg(GlobalStyle globalStyle) {
        if (CollectionUtils.isEmpty(globalStyle.getImgInfos())) {
            initializationImgInfo(globalStyle);
        }
        ImgInfo imgInfo = RandUtil.randElement(globalStyle.getImgInfos());
        assert imgInfo != null;
        fromHelper.addFrom(globalStyle, imgInfo);
        return BConstant.IMG_SYS_PATH + imgInfo.getPicUri();
    }

    public void initializationImgInfo(GlobalStyle globalStyle) {
        Set<ImgInfo> allImg = Sets.newHashSet();
        for (String keyword : globalStyle.getImgElements()) {
            List<ImgInfo> infos = imgInfoService.findByKeyword(keyword);
            allImg.addAll(infos);
        }
        List<String> elements = globalStyle.getImgElements()
                .stream()
                .filter(e -> e.length() < 3)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(allImg) && CollectionUtils.isEmpty(elements)) {
            List<ImgInfo> infos = imgInfoService.findByColorTypes(globalStyle.getColorInfo().getColorType());
            allImg.addAll(infos);
        }
        if (CollectionUtils.isEmpty(allImg)) {
            nullPicInfoService.insertKeyWords(globalStyle.getImgElements());
            throw new RuntimeException("图片缺失！" + globalStyle.getTitle());
        }
        List<ImgInfo> imgInfos = allImg.stream().filter(Objects::nonNull).collect(Collectors.toList());
        globalStyle.setImgInfos(imgInfos);
    }

    @SneakyThrows
    public String cutImgByWidthAndHeight(String imgPath, double width, double height) {
        chooseImgSmall(imgPath);
        Pair<Integer, Integer> widthAndHeight = findWidthAndHeight(imgPath);
        int oldWidth = widthAndHeight.getLeft();
        int oldHeight = widthAndHeight.getRight();
        if (oldHeight * 1.0 / oldWidth == height / width) {
            return imgPath;
        }
        String randPath = PathUtil.randPath(BConstant.JPG);
        if (oldHeight * 1.0 / oldWidth > height / width) {
            int newHeight = (int) Math.floor(height * (oldWidth / width));
            int top = (oldHeight - newHeight) / 2;
            ImgUtil.cut(
                    FileUtil.file(imgPath),
                    FileUtil.file(randPath),
                    new Rectangle(0, top, oldWidth, newHeight)
            );
        } else {
            int newWidth = (int) Math.floor(width * (oldHeight / height));
            int left = (oldWidth - newWidth) / 2;
            ImgUtil.cut(
                    FileUtil.file(imgPath),
                    FileUtil.file(randPath),
                    new Rectangle(left, 0, newWidth, oldHeight)
            );
        }
        Img.from(FileUtil.file(randPath))
                .setQuality(0.7)//压缩比率
                .write(FileUtil.file(randPath));
        return randPath;
    }

    private void chooseImgSmall(String imgPath) {
        Pair<Integer, Integer> widthAndHeight = findWidthAndHeight(imgPath);
        int oldWidth = widthAndHeight.getLeft();
        if (oldWidth > 1000) {
            ImgUtil.scale(
                    FileUtil.file(imgPath),
                    FileUtil.file(imgPath),
                    (float) (900 * 1.00 / oldWidth)
            );
        }
    }

    @SneakyThrows
    private Pair<Integer, Integer> findWidthAndHeight(String imgPath) {
        BufferedImage bufferedImage = ImageIO.read(new FileInputStream(FileUtil.file(imgPath)));
        return Pair.of(bufferedImage.getWidth(), bufferedImage.getHeight());
    }

}
