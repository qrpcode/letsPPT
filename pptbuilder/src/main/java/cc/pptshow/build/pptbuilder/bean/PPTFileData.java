package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PPT生成结果存储表
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "ppt_file_data")
public class PPTFileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private String globalStyle;

    private Integer pageId;

    private String elements;

    private String pptRegionPut;

    private String buildContext;

    private String colorContext;

}
