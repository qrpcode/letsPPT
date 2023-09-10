package cc.pptshow.build.pptbuilder.biz.builder.impl;

import cc.pptshow.build.pptbuilder.bean.*;
import cc.pptshow.build.pptbuilder.biz.builder.TemplateBuildBiz;
import cc.pptshow.build.pptbuilder.biz.read.ReadBiz;
import cc.pptshow.build.pptbuilder.dao.*;
import cc.pptshow.build.pptbuilder.domain.PPTTemplateElement;
import cc.pptshow.build.pptbuilder.domain.PPTTemplateGroup;
import cc.pptshow.build.pptbuilder.domain.PPTTemplatePage;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.qo.PageTemplateQo;
import cc.pptshow.build.pptbuilder.exception.PPTBuildException;
import cc.pptshow.build.pptbuilder.service.*;
import cc.pptshow.ppt.domain.PPTTextCss;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.domain.background.SerializableBackground;
import cc.pptshow.ppt.domain.border.ColorBorder;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.*;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;
import static cc.pptshow.build.pptbuilder.util.RandUtil.num2Decimal;

@Slf4j
@Service
public class TemplateBuildBizImpl implements TemplateBuildBiz {

    @Resource
    private PPTRegionMapper pptRegionMapper;

    @Resource
    private PPTRegionPutMapper pptRegionPutMapper;

    @Resource
    private PPTBlockPutService pptBlockPutService;

    @Resource
    private PPTBlockLineService pptBlockLineService;

    @Resource
    private PPTBlockTextMapper pptBlockTextMapper;

    @Resource
    private PPTBlockShapeMapper pptBlockShapeMapper;

    @Resource
    private PPTBlockMapper pptBlockMapper;

    @Resource
    private ReadBiz readBiz;

    @Resource
    private PPTBlockImgMapper pptBlockImgMapper;

    @Resource
    private PPTPageTypeService pptPageTypeService;

    @Resource
    private EnumPPTBackgroundService enumPPTBackgroundService;

    @Resource
    private EnumPPTStyleService enumPPTStyleService;

    public TemplateBuildBizImpl() {
    }

    private Long saveTextAsElement(PPTText pptElement, PPTTemplateElement pptTemplateElement) {
        List<PPTInnerLine> lineList = pptElement.getLineList();
        if (CollectionUtils.isEmpty(lineList)) {
            return null;
        }
        List<PPTInnerText> textList = lineList.get(0).getTextList();
        if (CollectionUtils.isEmpty(textList)) {
            return null;
        }
        String text = textList.get(0).getText();
        if (PPT_TIP.equals(text)) {
            return null;
        }
        double lineHeight = lineList.get(0).getCss().getLineHeight();
        int fontSize = textList.get(0).getCss().getFontSize();
        String align = lineList.get(0).getCss().getAlign();
        PPTTextCss pptTextCss = pptElement.getCss();
        Long id = savePPTBlockByText(pptTextCss);
        if (Objects.isNull(id)) {
            throw new PPTBuildException("保存过程失败！");
        }
        pptBlockTextSave(text, lineHeight, fontSize, align, id);
        return id;
    }

    private void pptBlockTextSave(String text, double lineHeight, int fontSize, String align, Long id) {
        PPTBlockText pptBlockText = new PPTBlockText();
        pptBlockText.setText(text);
        pptBlockText.setFontSize(fontSize);
        pptBlockText.setLineHeight(lineHeight);
        pptBlockText.setAlign(align);
        pptBlockText.setBlockId(id);
        pptBlockTextMapper.insertSelective(pptBlockText);
    }

    private Long savePPTBlockByText(PPTTextCss pptTextCss) {
        PPTBlock pptBlock = new PPTBlock();
        pptBlock.setHeightSize(num2Decimal(pptTextCss.getHeight()));
        pptBlock.setWidthSize(num2Decimal(pptTextCss.getWidth()));
        pptBlock.setLeftSize(num2Decimal(pptTextCss.getLeft()));
        pptBlock.setTopSize(num2Decimal(pptTextCss.getTop()));
        pptBlock.setPptBlockType(PPTBlockType.TEXT.getCode());
        pptBlockMapper.insertSelective(pptBlock);
        return pptBlock.getId();
    }

    private boolean isTemplateShape(Background background) {
        return Objects.nonNull(background)
                && ColorBackGround.class.equals(background.getClass())
                && TEMPLATE_COLOR.equals(((ColorBackGround) background).getColor());
    }

    private Long saveElementAsShape(PPTShape pptShape) {
        Background background = pptShape.getCss().getBackground();
        if (isTemplateShape(background)) {
            return null;
        }
        PPTBlock pptBlock = savePPTBlockByShape(pptShape);
        Long pptBlockId = pptBlock.getId();
        savePPTBlockShape(pptShape, pptBlockId);
        return pptBlock.getId();
    }

    private PPTBlock savePPTBlockByShape(PPTShape pptShape) {
        PPTBlock pptBlock = new PPTBlock();
        pptBlock.setPptBlockType(PPTBlockType.SHAPE.getCode());
        pptBlock.setLeftSize(num2Decimal(pptShape.getCss().getLeft()));
        pptBlock.setTopSize(num2Decimal(pptShape.getCss().getTop()));
        pptBlock.setWidthSize(num2Decimal(pptShape.getCss().getWidth()));
        pptBlock.setHeightSize(num2Decimal(pptShape.getCss().getHeight()));
        pptBlockMapper.insertSelective(pptBlock);
        return pptBlock;
    }

    private void savePPTBlockShape(PPTShape pptShape, Long pptBlockId) {
        PPTBlockShape pptBlockShape = new PPTBlockShape();
        pptBlockShape.setShapeXml(pptShape.getCss().getShape().prstGeomXmlGet());
        pptBlockShape.setAngle((int) Math.floor(pptShape.getCss().getAngle()));
        pptBlockShape.setBlockId(pptBlockId);
        pptBlockShape.setFlipX(BooleanUtils.toInteger(pptShape.getCss().isFlipX()));
        pptBlockShape.setFlipY(BooleanUtils.toInteger(pptShape.getCss().isFlipY()));
        if (pptShape.getCss().getBorder() instanceof ColorBorder) {
            double width = ((ColorBorder) pptShape.getCss().getBorder()).getWidth();
            pptBlockShape.setBorderSize(width);
        }
        pptBlockShape.setBackgroundColor(JSON.toJSONString(
                SerializableBackground.buildByBackground(pptShape.getCss().getBackground())));
        pptBlockShapeMapper.insertSelective(pptBlockShape);
    }

    @Override
    @Transactional
    public void buildPPTPage(PageTemplateQo pageTemplateQo) {
        assert !Strings.isNullOrEmpty(pageTemplateQo.getFile()) && pageTemplateQo.getPageIndex() > 0;
        List<PPTElement> pageElements = findElementsInPage(pageTemplateQo);
        saveAllGroup(pageTemplateQo.getGroupElements(), pageTemplateQo.getGroups(), pageTemplateQo.getPage(), pageElements);
    }

    private void saveAllGroup(List<PPTTemplateGroup> groupElements,
                              List<List<PPTTemplateElement>> groups,
                              PPTTemplatePage page,
                              List<PPTElement> pageElements) {
        checkIsAbleGroup(groupElements, groups);
        if (StringUtils.equals(groupElements.get(groupElements.size() - 1).getElement(), PAGE_ELEMENT)) {
            checkPageIsAllGroup(groups);
            List<Long> regionIds = saveRegionTemplate(exceptLastOne(groupElements), exceptLastOne(groups), pageElements, page);
            saveRegionPut(page, regionIds);
        } else {
            saveRegionTemplate(groupElements, groups, pageElements, page);
        }
    }

    private void saveRegionPut(PPTTemplatePage page, List<Long> regionIds) {
        PPTPageType pageType = pptPageTypeService.findByEnName(page.getPage());
        PPTRegionPut pptRegionPut = new PPTRegionPut();
        pptRegionPut.setSpecialType(page.getSpecial());
        pptRegionPut.setPptPageId(pageType.getId());
        pptRegionPut.setPptRegionIds(JOINER.join(regionIds));
        EnumPPTStyle style = enumPPTStyleService.findByCode(page.getStyle());
        pptRegionPut.setPageStyle(Optional.ofNullable(style).map(EnumPPTStyle::getId).orElse(null));
        EnumPPTBackground background = enumPPTBackgroundService.findByCode(page.getBackground());
        pptRegionPut.setPageBackground(Optional.ofNullable(background).map(EnumPPTBackground::getId).orElse(null));
        pptRegionPutMapper.insertSelective(pptRegionPut);
    }

    private List<Long> saveRegionTemplate(List<PPTTemplateGroup> groups,
                                          List<List<PPTTemplateElement>> templateElements,
                                          List<PPTElement> pageElements,
                                          PPTTemplatePage page) {
        List<Long> ids = Lists.newArrayList();
        for (int i = 0; i < groups.size(); i++) {
            ids.add(saveBlockGroup(groups, templateElements, i, pageElements, ids, page));
        }
        return ids;
    }

    /**
     * List内部顺序依赖，留意！！
     */
    private Long saveBlockGroup(List<PPTTemplateGroup> groupElements,
                                List<List<PPTTemplateElement>> groups,
                                int i,
                                List<PPTElement> pageElements,
                                List<Long> regionIds,
                                PPTTemplatePage page) {
        PPTTemplateGroup pptTemplateGroup = groupElements.get(i);
        List<PPTTemplateElement> templateElements = groups.get(i);
        List<Long> blockIds = Lists.newArrayList();
        Map<Integer, String> uuidMap = Maps.newHashMap();
        for (PPTTemplateElement pptTemplateElement : templateElements) {
            if (StringUtils.equals(pptTemplateElement.getType(), GROUP)) {
                Integer groupId = pptTemplateElement.getId();
                blockIds.add(saveBlockWithRegion(regionIds.get(groupId), groupElements.get(groupId)));
            } else if (StringUtils.equals(pptTemplateElement.getType(), PPTBlockType.SHAPE.getCode())) {
                blockIds.add(saveShapeBlock(pageElements.get(pptTemplateElement.getNum()), pptTemplateGroup));
            } else if (StringUtils.equals(pptTemplateElement.getType(), PPTBlockType.TEXT.getCode())) {
                blockIds.add(saveTextBlock(pageElements.get(pptTemplateElement.getNum()), pptTemplateGroup, pptTemplateElement));
            } else if (StringUtils.equals(pptTemplateElement.getType(), PPTBlockType.IMG.getCode())) {
                blockIds.add(saveImgBlock(pageElements.get(pptTemplateElement.getNum()), pptTemplateElement, pptTemplateGroup));
            } else if (StringUtils.equals(pptTemplateElement.getType(), PPTBlockType.LINE.getCode())) {
                blockIds.add(saveLineBlock(pageElements.get(pptTemplateElement.getNum()), pptTemplateElement, pptTemplateGroup));
            }
        }
        return savePPTRegion(pptTemplateGroup, blockIds, uuidMap, page);
    }

    private Long saveLineBlock(PPTElement pptElement,
                               PPTTemplateElement pptTemplateElement,
                               PPTTemplateGroup pptTemplateGroup) {
        PPTLine pptLine = (PPTLine) pptElement;
        PPTBlock pptBlock = new PPTBlock();
        pptBlock.setWidthSize(pptLine.getCss().getWidth());
        pptBlock.setHeightSize(pptLine.getCss().getHeight());
        pptBlock.setLeftSize(pptLine.getCss().getLeft() - pptTemplateGroup.getLeft());
        pptBlock.setTopSize(pptLine.getCss().getTop() - pptTemplateGroup.getTop());
        pptBlock.setPptBlockType(PPTBlockType.LINE.getCode());
        pptBlockMapper.insertSelective(pptBlock);

        PPTBlockLine pptBlockLine = new PPTBlockLine();
        pptBlockLine.setBlockId(pptBlock.getId());
        pptBlockLine.setLineType(pptLine.getCss().getType().getCode());
        pptBlockLine.setLineWidth(pptLine.getCss().getLineWidth());
        pptBlockLineService.insert(pptBlockLine);

        return pptBlock.getId();
    }

    private Long savePPTRegion(PPTTemplateGroup pptTemplateGroup,
                               List<Long> blockIds,
                               Map<Integer, String> uuidMap,
                               PPTTemplatePage page) {
        PPTRegion pptRegion = savePPTRegion(pptTemplateGroup, uuidMap, page);
        savePPTBlockPut(blockIds, pptRegion);
        return pptRegion.getId();
    }

    private void savePPTBlockPut(List<Long> blockIds, PPTRegion pptRegion) {
        PPTBlockPut pptBlockPut = new PPTBlockPut();
        pptBlockPut.setPptRegionId(pptRegion.getId());
        pptBlockPut.setPptBlockIds(JOINER.join(blockIds));
        pptBlockPutService.save(pptBlockPut);
    }

    private PPTRegion savePPTRegion(PPTTemplateGroup pptTemplateGroup,
                                    Map<Integer, String> uuidMap,
                                    PPTTemplatePage page) {
        PPTRegion pptRegion = new PPTRegion();
        pptRegion.setLeftSize(num2Decimal(pptTemplateGroup.getLeft()));
        pptRegion.setTopSize(num2Decimal(pptTemplateGroup.getTop()));
        pptRegion.setWidthSize(num2Decimal(pptTemplateGroup.getWidth()));
        pptRegion.setHeightSize(num2Decimal(pptTemplateGroup.getHeight()));
        pptRegion.setPptModelId(Integer.parseInt(pptTemplateGroup.getElement()));
        pptRegion.setAlignment(pptTemplateGroup.getAlignment());
        pptRegion.setSpecialType(page.getSpecial());
        if (!Strings.isNullOrEmpty(pptTemplateGroup.getAlignment())) {
            int key = Integer.parseInt(pptTemplateGroup.getAlignmentGroup());
            if (!uuidMap.containsKey(key)) {
                uuidMap.put(key, UUID.randomUUID().toString());
            }
            pptRegion.setAlignmentGroup(uuidMap.get(key));
        }
        pptRegionMapper.insertSelective(pptRegion);
        return pptRegion;
    }

    private Long saveImgBlock(PPTElement pptElement,
                              PPTTemplateElement pptTemplateElement,
                              PPTTemplateGroup pptTemplateGroup) {
        if (!(pptElement instanceof PPTImg)) {
            throw new RuntimeException("PPTImg: 录入模板可能触发了一个bug，用户录入信息和实际解析信息并不匹配！");
        }
        PPTImg pptImg = (PPTImg) pptElement;
        pptImg.getCss().setLeft(pptImg.getCss().getLeft() - pptTemplateGroup.getLeft())
                .setTop(pptImg.getCss().getTop() - pptTemplateGroup.getTop());
        return saveImgAsElement(pptImg, pptTemplateElement.getTheme(), pptTemplateElement.getShelter());
    }

    private Long saveImgAsElement(PPTImg pptImg, String theme, String shelter) {
        //存储pptBlock
        PPTBlock pptBlock = new PPTBlock();
        pptBlock.setLeftSize(pptImg.getCss().getLeft());
        pptBlock.setTopSize(pptImg.getCss().getTop());
        pptBlock.setWidthSize(pptImg.getCss().getWidth());
        pptBlock.setHeightSize(pptImg.getCss().getHeight());
        pptBlock.setPptBlockType(PPTBlockType.IMG.getCode());
        pptBlockMapper.insertSelective(pptBlock);
        //存储pptImg
        PPTBlockImg pptBlockImg = new PPTBlockImg();
        pptBlockImg.setBlockId(pptBlock.getId());
        pptBlockImg.setShelter(shelter);
        pptBlockImg.setTheme(theme);
        pptBlockImgMapper.insertSelective(pptBlockImg);
        return pptBlock.getId();
    }

    private Long saveTextBlock(PPTElement pptElement, PPTTemplateGroup pptTemplateGroup, PPTTemplateElement pptTemplateElement) {
        if (!(pptElement instanceof PPTText)) {
            throw new RuntimeException("PPTText: 录入模板可能触发了一个bug，用户录入信息和实际解析信息并不匹配！");
        }
        PPTText pptText = (PPTText) pptElement;
        pptText.getCss().setLeft(pptText.getCss().getLeft() - pptTemplateGroup.getLeft())
                .setTop(pptText.getCss().getTop() - pptTemplateGroup.getTop());
        return saveTextAsElement(pptText, pptTemplateElement);
    }

    private Long saveShapeBlock(PPTElement pptElement, PPTTemplateGroup pptTemplateGroup) {
        if (!(pptElement instanceof PPTShape)) {
            throw new RuntimeException("PPTShape: 录入模板可能触发了一个bug，用户录入信息和实际解析信息并不匹配！");
        }
        PPTShape pptShape = (PPTShape) pptElement;
        pptShape.getCss().setLeft(pptShape.getCss().getLeft() - pptTemplateGroup.getLeft())
                .setTop(pptShape.getCss().getTop() - pptTemplateGroup.getTop());
        return saveElementAsShape(pptShape);
    }

    private Long saveBlockWithRegion(Long regionId,
                                     PPTTemplateGroup pptTemplateGroup) {
        PPTBlock pptBlock = new PPTBlock();
        pptBlock.setLeftSize(pptTemplateGroup.getLeft());
        pptBlock.setTopSize(pptTemplateGroup.getTop());
        pptBlock.setWidthSize(pptTemplateGroup.getWidth());
        pptBlock.setHeightSize(pptTemplateGroup.getHeight());
        pptBlock.setRegionId(regionId);
        pptBlock.setPptBlockType(PPTBlockType.REGION.getCode());
        pptBlockMapper.insertSelective(pptBlock);
        return pptBlock.getId();
    }

    private <T> List<T> exceptLastOne(List<T> list) {
        return list.subList(0, list.size() - 1);
    }

    private void checkPageIsAllGroup(List<List<PPTTemplateElement>> groups) {
        List<PPTTemplateElement> elements = groups.get(groups.size() - 1);
        List<PPTTemplateElement> notGroup = elements.stream()
                .filter(p -> !p.getType().equals(GROUP.toUpperCase()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(notGroup)) {
            throw new PPTBuildException("如果您最后以页面形式存储，最后一组必须全部都是组合！");
        }
    }

    private void checkIsAbleGroup(List<PPTTemplateGroup> groupElements, List<List<PPTTemplateElement>> groups) {
        if (CollectionUtils.isEmpty(groupElements) || CollectionUtils.isEmpty(groups)) {
            throw new PPTBuildException("不能是空的！");
        }
        assert groupElements.size() == groups.size();
    }

    private List<PPTElement> findElementsInPage(PageTemplateQo pageTemplateQo) {
        List<List<PPTElement>> allPageElementsList = readBiz.readPPT(pageTemplateQo.getFile());
        return allPageElementsList.get(pageTemplateQo.getPageIndex());
    }

}
