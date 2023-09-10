package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 页面大类
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "ppt_page_type")
public class PPTPageType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 页面大类型名称，比如：目录页、图文页
     */
    private String pageTypeName;

    /**
     * 压面大类型名称，英文
     */
    private String pageTypeNameEn;

    /**
     * 是否需要统配标题
     * 0不需要  1需要
     * 一般只有图文页之类的内页需要，目录、首页、引言等这种类型页面不需要
     */
    private Integer needTitle;

    /**
     * 文本可能是居中的
     */
    private Integer maybeTextCenter;

    /**
     * 最多重复次数
     */
    private Integer maxRepeat;

}
