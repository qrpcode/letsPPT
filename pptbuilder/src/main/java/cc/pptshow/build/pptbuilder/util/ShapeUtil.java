package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.domain.ShapeFromXml;
import cc.pptshow.ppt.constant.Constant;

import static cc.pptshow.build.pptbuilder.util.TextUtil.getSubString;

public class ShapeUtil {

    private static final String BG_COLOR = "a:schemeClr val=\"accent6\"";
    private static final String COLOR_LEFT = "<a:srgbClr val=\"";
    private static final String COLOR_RIGHT = "\"/>";

    private static final String STYLE_COLOR = "3737FF";
    private static final String SET_OFF_COLOR = "FF3737";

    public static ShapeFromXml toShapeFromXml(String xml) {
        ShapeFromXml shapeFromXml = new ShapeFromXml();
        shapeFromXml.setPositionX(Integer.parseInt(getSubString(xml, "<a:off x=\"", "\" y=\"")));
        shapeFromXml.setPositionY(Integer.parseInt(getSubString(xml, "\" y=\"", "\"")));
        shapeFromXml.setWidth(Integer.parseInt(getSubString(xml, "<a:ext cx=\"", "\"")));
        shapeFromXml.setHeight(Integer.parseInt(getSubString(xml, "cy=\"", "\"")));

        if (xml.indexOf("<a:prstGeom") > 0) {
            shapeFromXml.setShapeXml("<a:prstGeom" + getSubString(xml, "<a:prstGeom", "</a:prstGeom>") + "</a:prstGeom>");
        } else {
            shapeFromXml.setShapeXml("<a:custGeom>" + getSubString(xml, "<a:custGeom>", "</a:custGeom>") + "</a:custGeom>");
        }
        if (xml.indexOf(SET_OFF_COLOR) > 0) {
            shapeFromXml.setIsBackground(true);
        }
        if (xml.indexOf(BG_COLOR) > 0) {
            shapeFromXml.setIsPic(true);
        }
        if (xml.indexOf("<a:srgbClr val=\"") > 0) {
            String color = getSubString(xml, COLOR_LEFT, COLOR_RIGHT);
            if (!STYLE_COLOR.equals(color) && !SET_OFF_COLOR.equals(color)) {
                shapeFromXml.setColor(color);
            }
        }

        if (xml.indexOf(" rot=\"") > 0) {
            shapeFromXml.setAngle(Integer.parseInt(getSubString(xml, " rot=\"", "\"")) / 60000);
        }
        return shapeFromXml;
    }
}
