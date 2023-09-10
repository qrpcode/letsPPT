package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.ppt.domain.PPTImgCss;
import cc.pptshow.ppt.domain.PPTShapeCss;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 过滤一些没必要存在的元素
 * 比如：文字框但是没有内容、图形框但是特别小根本看不到的
 */
@Slf4j
@Service
@Design(order = -1, needIteration = false)
public class EmptyFilterDesignBizImpl implements DesignBiz {

    /**
     * 至少需要0.2cm才有必要可见
     */
    private static final double MIN_SIZE = 0.2;

    @Override
    public DesignResponse design(DesignRequest request) {
        List<PPTElement> elements = request.getPptElements().stream()
                .filter(Objects::nonNull)
                .filter(element -> {
                    if (element instanceof PPTText) {
                        return Strings.isNotEmpty(((PPTText) element).findAllText());
                    } else if (element instanceof PPTShape) {
                        PPTShapeCss shapeCss = ((PPTShape) element).getCss();
                        return shapeCss.getHeight() > MIN_SIZE || shapeCss.getWidth() > MIN_SIZE;
                    } else if (element instanceof PPTImg) {
                        PPTImgCss imgCss = ((PPTImg) element).getCss();
                        return imgCss.getHeight() > MIN_SIZE || imgCss.getWidth() > MIN_SIZE;
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
        return new DesignResponse(elements);
    }
}
