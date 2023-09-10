package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.bean.HomePicShape;
import cc.pptshow.build.pptbuilder.domain.ShapeFromXml;

public class BeanUtil {

    public static HomePicShape shapeFromXml2HomePicShape(ShapeFromXml shapeFromXml) {
        HomePicShape homePicShape = new HomePicShape();
        homePicShape.setShapeXml(shapeFromXml.getShapeXml());
        homePicShape.setWidth(shapeFromXml.getWidth());
        homePicShape.setHeight(shapeFromXml.getHeight());
        homePicShape.setPositionX(shapeFromXml.getPositionX());
        homePicShape.setPositionY(shapeFromXml.getPositionY());
        homePicShape.setAngle(shapeFromXml.getAngle());
        if (shapeFromXml.getIsPic()) {
            homePicShape.setIsPic(1);
        } else {
            homePicShape.setIsPic(0);
        }
        if (shapeFromXml.getIsBackground()) {
            homePicShape.setIsBackground(1);
        } else {
            homePicShape.setIsBackground(0);
        }
        homePicShape.setColor(shapeFromXml.getColor());
        return homePicShape;
    }

}
