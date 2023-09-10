package cc.pptshow.build.pptbuilder.domain.qo;

import cc.pptshow.build.pptbuilder.domain.PPTTemplateElement;
import cc.pptshow.build.pptbuilder.domain.PPTTemplateGroup;
import cc.pptshow.build.pptbuilder.domain.PPTTemplatePage;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
public class PageTemplateQo {
    private List<List<PPTTemplateElement>> groups = Lists.newArrayList();
    private List<PPTTemplateGroup> groupElements = Lists.newArrayList();
    private PPTTemplatePage page;

    private String file;
    private Integer pageIndex;
}
