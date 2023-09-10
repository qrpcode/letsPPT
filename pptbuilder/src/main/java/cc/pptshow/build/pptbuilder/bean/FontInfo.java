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
@Table(name = "font_info")
public class FontInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 字体名称（网页显示名称）
     */
    private String fontName;

    /**
     * 字体标识名称（系统名称）
     */
    private String fontCode;

    /**
     * 版权说明地址
     */
    private String fromUrl;

    /**
     * 可以在哪些风格中出现
     * 大多都是中国风风格需要单独字体
     */
    private Integer pptStyleId;

    /**
     * 类型
     * 1-内页大面积文本  2-标题艺术文本
     */
    private Integer type;

}
