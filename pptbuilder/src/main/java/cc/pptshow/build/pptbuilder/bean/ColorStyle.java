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
@Table(name = "color_style")
public class ColorStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String color;

    private String colorStyleName;

    private String nearlyWord;

    /**
     * 命中的单词
     */
    private String matchWord;

}
