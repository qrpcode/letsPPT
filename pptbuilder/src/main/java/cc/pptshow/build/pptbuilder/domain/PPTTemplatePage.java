package cc.pptshow.build.pptbuilder.domain;

import lombok.Data;

@Data
public class PPTTemplatePage {

    private String page;

    private String style;

    private String background;

    private Integer special = 0;

}
