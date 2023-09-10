package cc.pptshow.build.pptbuilder.domain.qo;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.ppt.element.PPTElement;
import com.google.common.collect.Table;
import lombok.Data;

import java.util.List;

@Data
public class PPTPageBuildQo {

    private Integer pageTypeId;

    private GlobalStyle globalStyle;

    private List<PPTElement> pptElements;

    private PPTRegionPut pptRegionPut;

    private Table<PPTBlockPut, PPTBlock, PPTElement> buildContext;

    private Table<PPTBlockPut, String, String> colorContext;

    private Integer bigTitlePageCount;

}
