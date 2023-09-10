package cc.pptshow.build.pptbuilder.domain;

import lombok.Data;

@Data
public class PPTTemplateElement {

    /**
     * 传出的时候标注顺序，理论一个XML解析多次顺序稳定，可以以此确定元素
     */
    private Integer num;

    private double left;

    private double top;

    private double width;

    private double height;

    private String text;

    private String type;

    /**
     * 在文字和图片形式下可能存在
     */
    private String theme;

    /**
     * 渐变形式，只有在图片形式的时候存在
     */
    private String shelter;

    private Integer id;

}
