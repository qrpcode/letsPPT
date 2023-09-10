package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.ppt.element.impl.PPTInnerLine;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 如果用户当前文本只有四个字，而且换行后位置也够用那就换行显示
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT,
        order = 23,
        excludePage = {PPTPage.HOME})
public class TextBreakLineDesignBizImpl implements DesignBiz{

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        String allText = pptText.findAllText();
        if (allText.length() != 4) {
            return DesignResponse.buildByRequest(request);
        }
        if (pptText.findMinHeightSize() * 2 <= pptText.getCss().getHeight()) {
            pptText.getLineList().get(0).getTextList().get(0).setText(allText.substring(0, 2));
            PPTInnerLine cloneLine = pptText.getLineList().get(0).clone();
            cloneLine.getTextList().get(0).setText(allText.substring(2, 4));
            pptText.getLineList().add(cloneLine);
        }
        return DesignResponse.buildByRequest(request);
    }

}
