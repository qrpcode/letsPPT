package cc.pptshow.build.pptbuilder.domain.qo;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.bean.PPTPageModel;
import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.PPTRegionGroup;
import cc.pptshow.build.pptbuilder.domain.enums.Position;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BuilderQo implements Cloneable {

    private PPTRegion pptRegion;

    private Position position;

    private PPTPageModel pageModel;

    /**
     * PPT原始模板使用的blockPut信息
     */
    private PPTBlockPut originalBlockPut;

    /**
     * PPT当前场景筛选出的方案
     */
    private PPTBlockPut pptBlockPut;

    private PPTBlock pptBlock;

    private PPTRegionGroup pptRegionGroup;

    private Double leftSize;

    private Double topSize;

    private GlobalStyle globalStyle;

    @Override
    public BuilderQo clone() {
        BuilderQo builderQo = new BuilderQo();
        builderQo.setGlobalStyle(globalStyle);
        builderQo.setPptRegion(pptRegion);
        builderQo.setPageModel(pageModel);
        builderQo.setPosition(position);
        builderQo.setPptBlock(pptBlock);
        builderQo.setPptRegionGroup(pptRegionGroup);
        builderQo.setOriginalBlockPut(originalBlockPut);
        builderQo.setLeftSize(leftSize);
        builderQo.setTopSize(topSize);
        return builderQo;
    }

}
