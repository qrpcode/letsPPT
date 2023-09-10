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
@Table(name = "home_pic_decorate")
public class HomePicDecorate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 图片风格（对应 HomePicStyle ）
     */
    private Integer picStyle;

    /**
     * 图片样式（对应 HomePicCornerShape 或 HomePicSideShape ）
     */
    private Integer picShape;

    /**
     * 中文介绍，用于拼装PPT简介
     */
    private String aboutCn;

    /**
     * 英文介绍，用于拼装PPT简介
     */
    private String aboutEn;

}
