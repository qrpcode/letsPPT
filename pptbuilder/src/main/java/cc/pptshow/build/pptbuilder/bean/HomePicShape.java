package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NameStyle(Style.camelhump)
@Table(name = "home_pic_shape")
public class HomePicShape {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联homePic的id
     */
    private Long homePicDecorateId;

    /**
     * 排序，小在下，大在上
     */
    private Integer shapeOrder;

    /**
     * 是不是图片位置
     */
    private Integer isPic;

    /**
     * 是不是背景装饰类图片
     */
    private Integer isBackground;

    /**
     * 图形的xml信息
     */
    private String shapeXml;

    /**
     * 左侧位置
     */
    private Integer positionX;

    /**
     * 顶侧位置
     */
    private Integer positionY;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 高度
     */
    private Integer height;

    /**
     * 旋转角度
     */
    private Integer angle;

    /**
     * 是否预定颜色
     */
    private String color;

}
