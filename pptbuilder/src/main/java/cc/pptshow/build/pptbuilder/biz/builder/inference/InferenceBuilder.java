package cc.pptshow.build.pptbuilder.biz.builder.inference;

import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.ppt.show.PPTShow;
import cc.pptshow.ppt.show.PPTShowSide;

public interface InferenceBuilder {

    PPTShowSide buildPageByStyle(GlobalStyle globalStyle, PPTShow ppt);

}
