package cc.pptshow.build.pptbuilder.domain;

import lombok.Data;

@Data
public class PPTTemplateGroup {
    private double left;
    private double top;
    private double width;
    private double height;

    private String purpose;
    private String element;

    /**
     * 横向对齐
     */
    private String transverse;
    /**
     * 纵向对齐
     */
    private String portrait;

    /**
     * 如果type是GROUP的场景下会表示第几个group
     */
    private Integer id;

    private String alignment;

    private String alignmentGroup;
}
