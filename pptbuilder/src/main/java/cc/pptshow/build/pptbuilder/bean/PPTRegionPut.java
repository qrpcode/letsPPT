package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PPT区域元素摆放表
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "ppt_region_put")
public class PPTRegionPut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * PPT页面模块id
     */
    private Integer pptPageId;

    /**
     * PPT页面区域id列表，注意使用逗号分割
     */
    private String pptRegionIds;

    /**
     * 页面的风格
     */
    private Integer pageStyle;

    /**
     * 页面的背景形式
     */
    private Integer pageBackground;

    /**
     * 是否已经确认拥有实现
     */
    private Integer completeType;

    /**
     * 特殊类型
     * 默认是0如果不是0就表示只能用于特殊的场景下
     */
    private Integer specialType;

}
