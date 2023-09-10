package cc.pptshow.build.pptbuilder.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class NlpVo {

    private String text;

    private List<NlpItemVo> items;

}
