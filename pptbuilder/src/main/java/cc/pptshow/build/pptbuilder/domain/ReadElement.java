package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.domain.enums.ReadElementColor;
import lombok.Data;

@Data
public class ReadElement {
    private double left;
    private double top;
    private double width;
    private double height;
    private String text;
    private String color;
    private String type;

    private Integer num;

    public static ReadElement buildByLocation(ElementLocation elementLocation,
                                              ReadElementColor color,
                                              String text) {
        ReadElement readElement = new ReadElement();
        readElement.setLeft(elementLocation.getLeft());
        readElement.setTop(elementLocation.getTop());
        readElement.setWidth(elementLocation.getWidth());
        readElement.setHeight(elementLocation.getHeight());
        readElement.setColor(color.getColor());
        readElement.setText(text);
        readElement.setType(color.getCode());
        return readElement;
    }
}
