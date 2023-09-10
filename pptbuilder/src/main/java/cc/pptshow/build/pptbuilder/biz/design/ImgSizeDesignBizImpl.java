package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.util.PathUtil;
import cc.pptshow.ppt.element.impl.PPTImg;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 将所有的图片进行缩小到需要尺寸
 */
@Slf4j
@Service
@Design(type = PPTBlockType.IMG, order = 9)
public class ImgSizeDesignBizImpl /*implements DesignBiz*/ {

    private static final int SIZE_MULTIPLE = 100;

    //@Override
    @SneakyThrows
    public DesignResponse design(DesignRequest request) {
        PPTImg img = (PPTImg) request.getPptElement();
        if (StringUtils.equals(img.getCss().getName(), BConstant.ICON)) {
            return DesignResponse.buildByRequest(request);
        }
        String extName = FileUtil.extName(img.getFile());
        double height = img.getCss().getHeight();
        double width = img.getCss().getWidth();
        String path = PathUtil.randPath(extName);
        thumbnailImage(new File(img.getFile()), (int) (width * SIZE_MULTIPLE), (int) (height * SIZE_MULTIPLE), path, false);
        img.setFile(path);
        ImgUtil.scale(
                FileUtil.file(path),
                FileUtil.file(path),
                0.3f
        );
        log.info("[ImgSizeDesignBizImpl] wight: {}, height: {}, path: {}", width, height, path);
        return new DesignResponse(img);
    }

    /**
     * 根据图片路径生成缩略图
     *
     * @param imgFile 原图片路径
     * @param w       缩略图宽
     * @param h       缩略图高
     * @param newPath 新的路径
     * @param force   是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     */
    public void thumbnailImage(File imgFile, int w, int h, String newPath, boolean force) {
        if (imgFile.exists()) {
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames());
                String suffix = null;
                // 获取图片后缀
                if (imgFile.getName().contains(".")) {
                    suffix = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if (suffix == null || !types.toLowerCase().contains(suffix.toLowerCase())) {
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return;
                }
                log.debug("target image's size, width:{}, height:{}.", w, h);
                Image img = ImageIO.read(imgFile);
                if (!force) {
                    // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
                    int width = img.getWidth(null);
                    int height = img.getHeight(null);
                    if ((width * 1.0) / w < (height * 1.0) / h) {
                        if (width > w) {
                            h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w / (width * 1.0)));
                            log.debug("change image's height, width:{}, height:{}.", w, h);
                        }
                    } else {
                        if (height > h) {
                            w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h / (height * 1.0)));
                            log.debug("change image's width, width:{}, height:{}.", w, h);
                        }
                    }
                }
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
                g.dispose();
                // 将图片保存在原目录并加上前缀
                ImageIO.write(bi, suffix, new File(newPath));
            } catch (IOException e) {
                log.error("generate thumbnail image failed.", e);
            }
        } else {
            log.warn("the image is not exist.");
        }
    }

}
