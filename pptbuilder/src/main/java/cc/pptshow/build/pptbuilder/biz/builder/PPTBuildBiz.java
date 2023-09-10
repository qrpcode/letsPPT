package cc.pptshow.build.pptbuilder.biz.builder;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.domain.BuildRequire;

public interface PPTBuildBiz {

    FilePPTInfo buildByRequire(BuildRequire buildRequire);

    void buildByFileData(Long id);

}
