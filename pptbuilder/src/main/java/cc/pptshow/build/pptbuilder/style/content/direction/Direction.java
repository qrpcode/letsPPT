package cc.pptshow.build.pptbuilder.style.content.direction;

import cc.pptshow.build.pptbuilder.domain.qo.DirectionQo;
import cc.pptshow.ppt.element.PPTElement;

import java.util.List;

public interface Direction {

    List<PPTElement> toElements(DirectionQo directionQo);

}
