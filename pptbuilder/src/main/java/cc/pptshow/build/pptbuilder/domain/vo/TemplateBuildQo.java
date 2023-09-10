package cc.pptshow.build.pptbuilder.domain.vo;

import cc.pptshow.build.pptbuilder.domain.PPTTemplateElement;
import cc.pptshow.build.pptbuilder.domain.PPTTemplateGroup;
import cc.pptshow.build.pptbuilder.domain.PPTTemplatePage;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TemplateBuildQo {

    private List<PPTTemplateGroup> groups;

    private List<List<PPTTemplateElement>> elements;

    private PPTTemplatePage page;

    private List<Map<String, Object>> thisGroupElements;

}
