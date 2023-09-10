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
@Table(name = "content_title")
public class ContentTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * icon的svg，默认图片颜色是 #333333
     */
    private String iconSvg;

    /**
     * 内容标题
     */
    private String title;

    /**
     * 6个字的内容标题
     */
    private String titleLong;

    /**
     * 允许所处的位置
     * 0为任何位置都可以，其他合法指定为 1-4
     */
    private Integer ableOrder;
}
