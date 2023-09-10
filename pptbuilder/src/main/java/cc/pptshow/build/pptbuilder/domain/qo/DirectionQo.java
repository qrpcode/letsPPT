package cc.pptshow.build.pptbuilder.domain.qo;

import lombok.Data;

/**
 * 序号请求qo
 */
@Data
public class DirectionQo {
    /**
     * 序号id
     */
    private int id;
    private String title;
    private String iconFile;
    private double left;
    private double top;
    private String mainColor;
}
