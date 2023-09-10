package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PPT图案元素表
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "ppt_block_shape")
public class PPTBlockShape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long blockId;

    /**
     * 自定义图案的XML
     */
    private String shapeXml;

    /**
     * 旋转角度
     */
    private Integer angle;

    /**
     * 背景色规则
     */
    private String backgroundColor;

    /**
     * 边框的大小
     */
    private Double borderSize;

    /**
     * 水平翻转
     */
    private Integer flipX;

    /**
     * 垂直翻转
     */
    private Integer flipY;

}
