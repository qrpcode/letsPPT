package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;

public interface DesignBiz {

    DesignResponse design(DesignRequest request);

}
