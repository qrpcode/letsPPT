package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PPT页面结构页
 * 比如：左侧标题右侧目录项（目录页系统下）
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "ppt_page_model")
public class PPTPageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * PPT具体页面id
     */
    private Integer pptPageTypeId;

    /**
     * PPT页面结构详细信息
     */
    private String pptPageModelName;

    /**
     * 是否使用icon进行填充
     */
    private Integer fillIcon;

    /**
     * 是否使用LOGO进行填充
     */
    private Integer fillLogo;

    /**
     * 是否重复
     */
    private Integer needRepeat;

    /**
     * 必须使用原始元素禁止替换
     */
    private Integer noSubstitution;

    /**
     * 普通的文本
     */
    private Integer normalText;

}
