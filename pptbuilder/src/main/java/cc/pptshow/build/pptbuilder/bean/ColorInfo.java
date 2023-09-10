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
@Table(name = "color_info")
public class ColorInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String colorName;

    /**
     * 为FFFFFF可以视为没有背景
     */
    private String backgroundColor;

    /**
     * 背景颜色的分类
     */
    private Integer backgroundColorType;

    /**
     * 一定存在
     */
    private String fromColor;

    /**
     * 可能为空
     */
    private String toColor;

    /**
     * 逗号分割，可能多组
     */
    private String colorType;


}
