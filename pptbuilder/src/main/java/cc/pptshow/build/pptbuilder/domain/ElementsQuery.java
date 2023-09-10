package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.ppt.element.PPTElement;
import lombok.Data;

import java.util.List;

@Data
public class ElementsQuery {

    private List<PPTElement> tops;

    private List<PPTElement> backgrounds;

}
