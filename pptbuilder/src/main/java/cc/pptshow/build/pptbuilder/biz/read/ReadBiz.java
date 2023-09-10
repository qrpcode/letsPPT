package cc.pptshow.build.pptbuilder.biz.read;

import cc.pptshow.build.pptbuilder.domain.PPTPageRead;
import cc.pptshow.ppt.element.PPTElement;

import java.util.List;

public interface ReadBiz {

    List<List<PPTElement>> readPPT(String filePath);

    List<PPTPageRead> readPPTToReadElements(String filePath);

    void cachePPTImg();

}
