package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PPT页面区域
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "ppt_region")
public class PPTRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * PPT素材类型id
     */
    private Integer pptModelId;

    /**
     * 距离页面左侧距离
     */
    private Double leftSize;

    /**
     * 距离页面顶侧距离
     */
    private Double topSize;

    /**
     * 宽度
     */
    private Double widthSize;

    /**
     * 高度
     */
    private Double heightSize;

    /**
     * 指定位置的关联组合标识
     * 可能是空、left、right
     * 当为空表示并不是组合关联元素
     */
    private String alignment;

    /**
     * 如果是组合关联元素，这一项需要储存uuid，相同表示同组
     */
    private String alignmentGroup;

    /**
     * 特殊类型
     * 默认是0如果不是0就表示只能用于特殊的场景下
     */
    private Integer specialType;

}
