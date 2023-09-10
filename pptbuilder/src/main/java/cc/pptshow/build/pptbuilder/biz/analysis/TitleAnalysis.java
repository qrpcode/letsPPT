package cc.pptshow.build.pptbuilder.biz.analysis;

import cc.pptshow.build.pptbuilder.domain.StyleAnalysis;
import cc.pptshow.build.pptbuilder.domain.vo.NlpVo;

public interface TitleAnalysis {

    StyleAnalysis analysisTitle(Long titleId);

    StyleAnalysis analysisTitle(String title);

    String filterIgnoreWord(String title);

    NlpVo nlpText(String text);

}
