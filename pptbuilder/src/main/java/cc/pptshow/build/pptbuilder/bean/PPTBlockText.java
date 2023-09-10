package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PPT文字元素表
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "ppt_block_text")
public class PPTBlockText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long blockId;

    /**
     * 文字的大小
     */
    private Integer fontSize;

    /**
     * 行高
     */
    private Double lineHeight = 1.5;

    /**
     * 文本内容
     */
    private String text;

    /**
     * 对齐格式
     */
    private String align;

    /**
     * 文字的类型
     */
    private String theme;

    public static PPTBlockText buildByBlockId(long id) {
        PPTBlockText pptBlockText = new PPTBlockText();
        pptBlockText.setBlockId(id);
        pptBlockText.setLineHeight(null);
        return pptBlockText;
    }

}
