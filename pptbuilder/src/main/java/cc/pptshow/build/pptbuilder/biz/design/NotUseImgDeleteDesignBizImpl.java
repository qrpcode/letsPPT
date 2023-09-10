package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Design(order = 23, needIteration = false, type = PPTBlockType.IMG)
public class NotUseImgDeleteDesignBizImpl implements DesignBiz {

    @Override
    public DesignResponse design(DesignRequest request) {
        List<PPTElement> pptElements = request.getPptElements().stream()
                .filter(element -> !isNotUseImg(element))
                .collect(Collectors.toList());
        return new DesignResponse(pptElements);
    }

    private boolean isNotUseImg(PPTElement pptElement) {
        if (Objects.nonNull(pptElement) && pptElement instanceof PPTImg) {
            PPTImg pptImg = (PPTImg) pptElement;
            return pptImg.getCss().getLeft() == 0 && pptImg.getCss().getTop() == 0
                    && pptImg.getCss().getHeight() < 4 && pptImg.getCss().getWidth() < 8.5;
        }
        return false;
    }

}
