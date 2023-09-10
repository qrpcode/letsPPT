package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PPT元素表
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "ppt_block")
public class PPTBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * PPT元素类型
     * 目前只允许4种类型：
     *      IMG 图片
     *      SHAPE 图形 - 详细设计需要查看关联表ppt_element_shape
     *      TEXT 文本 - 详细设计需要查看关联表ppt_element_text
     *      REGION 嵌套
     */
    private String pptBlockType;

    /**
     * ppt_region对应表的id，仅在REGION场景下使用，可空
     */
    private Long regionId;

    private Double leftSize;

    private Double topSize;

    private Double widthSize;

    private Double heightSize;

}
