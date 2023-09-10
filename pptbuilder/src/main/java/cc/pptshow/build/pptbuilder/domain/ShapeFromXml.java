package cc.pptshow.build.pptbuilder.domain;

import lombok.Data;

@Data
public class ShapeFromXml {
    private Integer positionX;
    private Integer positionY;
    private Integer height;
    private Integer width;
    private Integer angle;
    private String shapeXml;
    private String color;
    private Boolean isPic = false;
    private Boolean isBackground = false;
}
