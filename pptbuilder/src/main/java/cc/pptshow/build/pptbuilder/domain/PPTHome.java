package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.show.PPTShowSide;
import lombok.Data;

import java.util.List;

@Data
public class PPTHome {
    private PPTShowSide pptShowSide;
    private List<PPTElement> shapes;
}
