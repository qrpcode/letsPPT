package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.bean.PPTPageModel;
import cc.pptshow.build.pptbuilder.bean.PPTPageType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PPTPageTypeVo extends PPTPageType {

    private List<PPTPageModel> models;

    public PPTPageTypeVo(PPTPageType pageType) {
        this.setId(pageType.getId());
        this.setPageTypeName(pageType.getPageTypeName());
        this.setPageTypeNameEn(pageType.getPageTypeNameEn());
        this.setNeedTitle(pageType.getNeedTitle());
    }

}
