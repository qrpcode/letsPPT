package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.ElementLocation;
import cc.pptshow.build.pptbuilder.domain.ReadElement;
import cc.pptshow.build.pptbuilder.domain.enums.ReadElementColor;
import cc.pptshow.build.pptbuilder.style.SideEnum;
import cc.pptshow.ppt.constant.Constant;
import cc.pptshow.ppt.domain.*;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.domain.background.ImgBackground;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTLine;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import cc.pptshow.ppt.show.PPTShow;
import cc.pptshow.ppt.show.PPTShowSide;
import cc.pptshow.ppt.util.FileUtil;
import cc.pptshow.ppt.util.PPT2ImgUtil;
import cn.hutool.core.img.ImgUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;

@Slf4j
public class PositionUtil {

    public static final double CORNER_SIZE = 4.00;
    public static final double CORNER_ALL_SIZE = 7.00;

    public static double titleMaxLength(List<PPTElement> elements, SideEnum titleSide) {
        double maxLength = BConstant.PAGE_WIDTH;
        for (PPTElement element : elements) {
            if (element instanceof PPTShape) {
                PPTShape shape = (PPTShape) element;
                if (shape.getCss().getWidth() > BConstant.MIN_BG_SIZE * BConstant.PAGE_WIDTH) {
                    continue;
                }
                if ((shape.getCss().getTop() > HOME_TITLE_TOP && shape.getCss().getTop() < HOME_TITLE_BOTTOM)
                        || (shape.getCss().getTop() + shape.getCss().getHeight() > HOME_TITLE_TOP
                        && shape.getCss().getTop() + shape.getCss().getHeight() < HOME_TITLE_BOTTOM)
                        || (shape.getCss().getTop() < HOME_TITLE_BOTTOM
                        && shape.getCss().getTop() + shape.getCss().getHeight() > HOME_TITLE_BOTTOM)) {
                    if (titleSide.equals(SideEnum.LEFT)) {
                        //如果元素不在核心半区，视为装饰元素，忽略掉
                        if (shape.getCss().getLeft() < HALF * PAGE_WIDTH) {
                            continue;
                        }

                        maxLength = Math.min(maxLength, shape.getCss().getLeft());
                    } else {
                        if (titleSide.equals(SideEnum.RIGHT)) {
                            //如果元素不在核心半区，视为装饰元素，忽略掉
                            if (shape.getCss().getLeft() + shape.getCss().getWidth() < HALF * PAGE_WIDTH) {
                                continue;
                            }

                            maxLength = Math.min(maxLength, PAGE_WIDTH - shape.getCss().getLeft() - shape.getCss().getWidth());
                        }
                    }
                }
            }
        }
        return maxLength;
    }

    public static List<PPTElement> toLeftTopAndRightBottomCornerShape(List<PPTElement> elements) {
        return toCornerShape(elements, true, false, false, false);
    }

    private static List<PPTElement> toCornerShape(List<PPTElement> elements,
                                                  boolean needLeftTop,
                                                  boolean needLeftBottom,
                                                  boolean needRightTop,
                                                  boolean needRightBottom) {

        List<PPTElement> returnList = Lists.newArrayList();

        List<List<PPTElement>> group = toElementGroup(elements);
        List<List<PPTElement>> noImgGroup = Lists.newArrayList();
        for (List<PPTElement> pptElements : group) {
            boolean noMatch = pptElements.stream()
                    .noneMatch(pptElement -> pptElement.getClass().equals(PPTImg.class)
                            || (pptElement.getClass().equals(PPTShape.class)
                            && ((PPTShape) pptElement).getCss()
                            .getBackground().getClass().equals(ImgBackground.class)));
            if (noMatch) {
                noImgGroup.add(pptElements);
            }
        }
        //如果原来就有在边角上的就优先他们
        if (needLeftTop) {
            boolean haveCornerShape = false;
            for (List<PPTElement> pptElements : noImgGroup) {
                //需要满足边角的条件
                if (getLeftTopX(pptElements) <= 0 && getLeftTopY(pptElements) <= 0
                        && getRightBottomX(pptElements) - getLeftTopX(pptElements) < PAGE_WIDTH * PAGE_CORNER_MAX_X
                        && getRightBottomY(pptElements) - getLeftTopY(pptElements) < PAGE_HEIGHT * PAGE_CORNER_MAX_Y) {
                    returnList.addAll(pptElements);
                    noImgGroup.remove(pptElements);
                    haveCornerShape = true;
                    break;
                }
            }
            if (!haveCornerShape) {
                List<List<PPTElement>> leftTopGroups = noImgGroup.stream()
                        .filter(g -> getRightBottomX(g) <= PAGE_WIDTH && getRightBottomY(g) < PAGE_WIDTH)
                        .collect(Collectors.toList());
                List<PPTElement> cornerShape = leftTopGroups.get(RandUtil.round(0, leftTopGroups.size() - 1));
                returnList.addAll(toLeftTopCorner(cornerShape));
            }
        }

        return returnList;
    }

    /**
     * 把一组正常的图形缩小成为左上角的一坨
     */
    private static List<PPTElement> toLeftTopCorner(List<PPTElement> cornerShape) {
        double leftTopX = getLeftTopX(cornerShape);
        double leftTopY = getLeftTopY(cornerShape);
        double rightBottomX = getRightBottomX(cornerShape);
        double rightBottomY = getRightBottomY(cornerShape);
        double leftRealX = leftTopX < 0 ? 0 : leftTopX;
        double leftRealY = leftTopY < 0 ? 0 : leftTopY;
        double rightRealX = Math.min(rightBottomX, PAGE_WIDTH);
        double rightRealY = Math.min(rightBottomY, PAGE_HEIGHT);

        double realWidth = rightRealX - leftRealX;
        double realHeight = rightRealY - leftRealY;

        log.info("[图像缩放] leftTopX:{}, leftTopY:{}, rightBottomX:{}, rightBottomY:{}," +
                        " leftRealX:{}, leftRealY:{}, rightRealX:{}, rightRealY:{}, realWidth:{}, realHeight:{}",
                leftRealX, leftRealY, rightBottomX, rightBottomY, leftRealX, leftRealY, rightRealX, rightRealY,
                realWidth, realHeight);

        double scalingRatio = Math.max(realWidth, realHeight) / CORNER_ALL_SIZE;
        double scalingX;
        double scalingY;
        double angle = 0;
        if (realWidth > realHeight) {
            scalingX = CORNER_SIZE - CORNER_ALL_SIZE;
            scalingY = (CORNER_SIZE - CORNER_ALL_SIZE) * (realHeight / CORNER_SIZE);
        } else {
            scalingX = (CORNER_SIZE - CORNER_ALL_SIZE) * (realWidth / CORNER_SIZE);
            scalingY = CORNER_SIZE - CORNER_ALL_SIZE;
        }
        if (rightRealX == PAGE_WIDTH || rightRealY == PAGE_HEIGHT) {
            log.info("[旋转提醒] 发现边界图形，已经旋转");
            angle = 180;
        }
        if (realHeight * scalingRatio + scalingY > CORNER_SIZE) {
            scalingY = CORNER_SIZE - (realHeight * scalingRatio);
        }
        if (realWidth * scalingRatio + scalingX > CORNER_SIZE) {
            scalingX = CORNER_SIZE - (realWidth * scalingRatio);
        }
        return moveShape(cornerShape, scalingRatio, scalingX, scalingY, angle);
    }

    /**
     * 移动一组图形
     *
     * @param cornerShape  图形集合
     * @param scalingRatio 缩放比例
     * @param scalingX     左侧位置
     * @param scalingY     顶侧位置
     * @param angle        角度
     * @return 返回新集合
     */
    public static List<PPTElement> moveShape(List<PPTElement> cornerShape,
                                             double scalingRatio,
                                             double scalingX,
                                             double scalingY, double angle) {
        log.info("[移动元素] scalingRatio:{}, scalingX:{}, scalingY:{}", scalingRatio, scalingX, scalingY);
        double leftTopX = getLeftTopX(cornerShape);
        double leftTopY = getLeftTopY(cornerShape);

        List<PPTElement> newList = Lists.newArrayList();

        for (PPTElement pptElement : cornerShape) {
            if (pptElement.getClass().equals(PPTShape.class)) {
                PPTShapeCss css = ((PPTShape) pptElement).getCss();
                css.setWidth(css.getWidth() * scalingRatio);
                css.setHeight(css.getHeight() * scalingRatio);
                css.setLeft((css.getLeft() - leftTopX) * scalingRatio + scalingX);
                css.setTop((css.getTop() - leftTopY) * scalingRatio + scalingY);
                css.setAngle((css.getAngle() + angle) % 360);
                newList.add(pptElement);
            } else if (pptElement.getClass().equals(PPTImg.class)) {
                PPTImgCss css = ((PPTImg) pptElement).getCss();
                css.setWidth(css.getWidth() * scalingRatio);
                css.setHeight(css.getHeight() * scalingRatio);
                css.setLeft((css.getLeft() - leftTopX) * scalingRatio + scalingX);
                css.setTop((css.getTop() - leftTopY) * scalingRatio + scalingY);
                css.setAngle((css.getAngle() + angle) % 360);
                newList.add(pptElement);
            }
        }
        return newList;
    }

    private static List<List<PPTElement>> toElementGroup(List<PPTElement> elements) {
        //区域分组，不重叠区域为一组
        List<List<PPTElement>> elementGroup = Lists.newArrayList();

        while (CollectionUtils.isNotEmpty(elements)) {
            List<PPTElement> loop = Lists.newArrayList();
            loop.add(elements.get(0));
            elements.remove(0);
            int lastSize = 0;
            while (loop.size() != lastSize) {
                lastSize = loop.size();
                double leftTopX = getLeftTopX(loop);
                double leftTopY = getLeftTopY(loop);
                double rightBottomX = getRightBottomX(loop);
                double rightBottomY = getRightBottomY(loop);

                List<PPTElement> newPutIn = Lists.newArrayList();

                for (PPTElement element : elements) {
                    if (element.getClass().equals(PPTShape.class)) {
                        PPTShapeCss css = ((PPTShape) element).getCss();
                        if (haveOverlap(leftTopX, leftTopY, rightBottomX - leftTopX, rightBottomY - leftTopY,
                                css.getLeft(), css.getTop(), css.getWidth(), css.getHeight())) {
                            newPutIn.add(element);
                        }
                    } else if (element.getClass().equals(PPTImg.class)) {
                        PPTImgCss css = ((PPTImg) element).getCss();
                        if (haveOverlap(leftTopX, leftTopY, rightBottomX - leftTopX, rightBottomY - leftTopY,
                                css.getLeft(), css.getTop(), css.getWidth(), css.getHeight())) {
                            newPutIn.add(element);
                        }
                    }
                }

                elements.removeAll(newPutIn);
                loop.addAll(newPutIn);
            }

            elementGroup.add(loop);
        }

        return elementGroup;
    }

    public static boolean haveOverlap(double left1, double top1, double width1, double height1,
                                      double left2, double top2, double width2, double height2) {
        return (Math.min(left1 + width1, left2 + width2) > Math.max(left1, left2) &&
                Math.min(top1 + height1, top2 + height2) > Math.max(top1, top2));
    }

    public static double calculateOverlappingArea(PPTElement first, PPTElement second) {
        ElementLocation firstLocation = findLocationByElement(first);
        ElementLocation secondLocation = findLocationByElement(second);
        assert firstLocation != null;
        assert secondLocation != null;
        double x = Math.min(firstLocation.getRight(), secondLocation.getRight())
                - Math.max(firstLocation.getLeft(), secondLocation.getLeft());
        double y = Math.min(firstLocation.getBottom(), secondLocation.getBottom())
                - Math.max(firstLocation.getTop(), secondLocation.getTop());
        if (x <= 0 || y <= 0) {
            return 0;
        }
        return x * y;
    }

    public static double calculateElementArea(PPTElement element) {
        ElementLocation elementLocation = findLocationByElement(element);
        assert elementLocation != null;
        return (elementLocation.getWidth() * elementLocation.getHeight());
    }

    public static ReadElement findReadElementByElement(PPTElement element) {
        ElementLocation locationByElement = findLocationByElement(element);
        assert locationByElement != null;
        String text = " ";
        ReadElementColor color = ReadElementColor.SHAPE;
        if (element instanceof PPTText) {
            color = ReadElementColor.TEXT;
            String elementText = ((PPTText) element).findAllText();
            if (Strings.isNotEmpty(elementText)) {
                text = elementText;
            }
        } else if (element instanceof PPTImg) {
            color = ReadElementColor.IMG;
        } else if (element instanceof PPTLine) {
            color = ReadElementColor.LINE;
        }
        return ReadElement.buildByLocation(locationByElement, color, text);
    }

    public static ElementLocation findLocationByElement(PPTElement element) {
        if (element instanceof PPTText) {
            PPTTextCss css = ((PPTText) element).getCss();
            return ElementLocation.builder()
                    .left(css.getLeft())
                    .top(css.getTop())
                    .height(css.getHeight())
                    .width(css.getWidth())
                    .build();
        } else if (element instanceof PPTShape) {
            PPTShapeCss css = ((PPTShape) element).getCss();
            return ElementLocation.builder()
                    .left(css.getLeft())
                    .top(css.getTop())
                    .height(css.getHeight())
                    .width(css.getWidth())
                    .build();
        } else if (element instanceof PPTImg) {
            PPTImgCss css = ((PPTImg) element).getCss();
            return ElementLocation.builder()
                    .left(css.getLeft())
                    .top(css.getTop())
                    .height(css.getHeight())
                    .width(css.getWidth())
                    .build();
        } else if (element instanceof PPTLine) {
            PPTLineCss css = ((PPTLine) element).getCss();
            return ElementLocation.builder()
                    .left(css.getLeft())
                    .top(css.getTop())
                    .height(css.getHeight())
                    .width(css.getWidth())
                    .build();
        }
        return null;
    }

    private static double getLeftTopX(List<PPTElement> loop) {
        double x = Integer.MAX_VALUE;
        for (PPTElement pptElement : loop) {
            if (pptElement.getClass().equals(PPTShape.class)) {
                x = Math.min(((PPTShape) pptElement).getCss().getLeft(), x);
            } else if (pptElement.getClass().equals(PPTImg.class)) {
                x = Math.min(((PPTImg) pptElement).getCss().getLeft(), x);
            }
        }
        return x;
    }

    private static double getLeftTopY(List<PPTElement> loop) {
        double y = Integer.MAX_VALUE;
        for (PPTElement pptElement : loop) {
            if (pptElement.getClass().equals(PPTShape.class)) {
                y = Math.min(((PPTShape) pptElement).getCss().getTop(), y);
            } else if (pptElement.getClass().equals(PPTImg.class)) {
                y = Math.min(((PPTImg) pptElement).getCss().getTop(), y);
            }
        }
        return y;
    }

    private static double getRightBottomX(List<PPTElement> loop) {
        double x = Integer.MIN_VALUE;
        for (PPTElement pptElement : loop) {
            if (pptElement.getClass().equals(PPTShape.class)) {
                x = Math.max(((PPTShape) pptElement).getCss().getLeft() + ((PPTShape) pptElement).getCss().getWidth(), x);
            } else if (pptElement.getClass().equals(PPTImg.class)) {
                x = Math.max(((PPTImg) pptElement).getCss().getLeft() + ((PPTImg) pptElement).getCss().getWidth(), x);
            }
        }
        return x;
    }

    private static double getRightBottomY(List<PPTElement> loop) {
        double y = Integer.MIN_VALUE;
        for (PPTElement pptElement : loop) {
            if (pptElement.getClass().equals(PPTShape.class)) {
                y = Math.max(((PPTShape) pptElement).getCss().getTop() + ((PPTShape) pptElement).getCss().getHeight(), y);
            } else if (pptElement.getClass().equals(PPTImg.class)) {
                y = Math.max(((PPTImg) pptElement).getCss().getTop() + ((PPTImg) pptElement).getCss().getHeight(), y);
            }
        }
        return y;
    }

    public static List<PPTElement> toLeftPic(List<PPTElement> elements) {
        List<List<PPTElement>> group = toElementGroup(elements);
        List<PPTElement> imgGroup = Lists.newArrayList();
        for (List<PPTElement> pptElements : group) {
            if (pptElements.stream()
                    .anyMatch(pptElement -> pptElement.getClass().equals(PPTImg.class)
                            || (pptElement.getClass().equals(PPTShape.class)
                            && ((PPTShape) pptElement).getCss()
                            .getBackground().getClass().equals(ImgBackground.class)))) {
                imgGroup.addAll(pptElements);
            }
        }
        return imgGroup;
    }

    public static boolean isIconShape(PPTElement pptElement) {
        if (pptElement instanceof PPTShape && Objects.isNull(((PPTShape) pptElement).getCss())) {
            log.error(JSON.toJSONString(pptElement));
        }
        return pptElement instanceof PPTShape
                && StringUtils.equals(((PPTShape) pptElement).getCss().getName(), ICON);
    }

    /**
     * 有一些图形本身就是一个外边框，但是当成图形处理并不合理，所以我们加一个判断
     */
    public static boolean isCenterHaveColorShape(PPTShape pptShape) {
        PPTShape cloneShape = pptShape.clone();
        cloneShape.getCss().setLeft(0).setTop(0).setBackground(ColorBackGround.buildByColor("000000"));
        String img = buildPPTImgByShape(cloneShape);
        String imgFile = img + Constant.SEPARATOR + "1.png";
        String cutImgFile = img + Constant.SEPARATOR + "2.png";

        double left = cloneShape.getCss().getWidth() / 4;
        double top = cloneShape.getCss().getHeight() / 4;
        ImgUtil.cut(
                cn.hutool.core.io.FileUtil.file(imgFile),
                cn.hutool.core.io.FileUtil.file(cutImgFile),
                new Rectangle(size2Px(left), size2Px(top), size2Px(left) * 2, size2Px(top) * 2)
        );
        log.info("[图片剪切] imgFile: {}, cutImgFile: {}", imgFile, cutImgFile);
        return isSimpleColorImg(cutImgFile, 0);
    }

    public static boolean isSimpleColorImg(String imgPath, float percent) {
        try {
            BufferedImage src = ImageIO.read(new File(imgPath));
            int height = src.getHeight();
            int width = src.getWidth();
            int count = 0, pixTemp = 0, pixel = 0;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    pixel = src.getRGB(i, j);
                    if (pixel == pixTemp)
                        count++;
                    else
                        count = 0;
                    if ((float) count / (height * width) >= percent)
                        return true;
                    pixTemp = pixel;
                }
            }
            return false;
        } catch (Throwable t) {
            log.error("[深色检验] 检验出错！", t);
            return false;
        }
    }

    private static String buildPPTImgByShape(PPTShape cloneShape) {
        String filePath = FileUtil.tmpdir() + FileUtil.uuid();
        String pptFilePath = filePath + ".pptx";
        PPTShow pptShow = new PPTShow();
        PPTShowSide side = PPTShowSide.build();
        side.add(cloneShape);
        pptShow.add(side);
        pptShow.toFile(pptFilePath);
        File ppt = new File(pptFilePath);
        String img = filePath + "pic";
        PPT2ImgUtil.converPPTtoImage(ppt, img, "png", 3);
        cn.hutool.core.io.FileUtil.del(pptFilePath);
        return img;
    }

    /**
     * 默认3倍缩放
     */
    private static int size2Px(double size) {
        return (int) Math.ceil(size * SIZE_PX);
    }

    /**
     * 相对相交比例为多少
     */
    public static double overlapProportion(double top, double height, double top2, double height2) {
        double topMax = Math.max(top, top2);
        double bottomMin = Math.min(top + height, top2 + height2);
        double size = bottomMin - topMax;
        double realSize = size > 0 ? size : 0;
        return (2 * realSize) / (height + height2 - (2 * realSize));
    }

    public static boolean isImg(PPTElement pptElement) {
        return Objects.nonNull(pptElement)
                && (
                pptElement instanceof PPTImg
                        || (
                        pptElement instanceof PPTShape
                                &&
                                Objects.nonNull(((PPTShape) pptElement).getCss())
                                &&
                                ((PPTShape) pptElement).getCss().getBackground() instanceof ImgBackground
                )
        );
    }

}
