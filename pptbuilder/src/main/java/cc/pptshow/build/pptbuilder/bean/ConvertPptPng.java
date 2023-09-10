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
@Table(name = "convert_ppt_png")
public class ConvertPptPng {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;

    private String imgPath;

    /**
     * 0 - 待分配
     * 1 - 已经分配机器但是还没转换完成
     * 2 - 转换完成
     * -1 - 转换失败
     */
    private int state;

    private String object;

}
