package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.domain.vo.NlpVo;
import lombok.Data;

import java.util.List;

@Data
public class StyleAnalysis {

    private String color;

    private String backgroundColor;

    private List<String> elements;

    private List<String> style;

    private List<String> newTitles;

    /**
     * 联网查询获得的相关颜色
     */
    private List<RGB> webColor;

    /**
     * NLP语义解析结果
     */
    private NlpVo nlpVo;


}
