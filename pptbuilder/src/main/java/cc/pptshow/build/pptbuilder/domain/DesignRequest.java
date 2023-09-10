package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.show.PPTShowSide;
import lombok.Data;

import java.util.List;

@Data
public class DesignRequest {

    private List<PPTElement> pptElements;

    private PPTElement pptElement;

    private GlobalStyle globalStyle;

    private Canvas canvas;

    private Integer pageId;

    /**
     * 上下文bean，会下一次继续传递过去
     */
    private List<Object> contexts;

    private PPTShowSide pptShowSide;

    private PPTRegionPut pptRegionPut;

    private int bigTitleNumber;

}
