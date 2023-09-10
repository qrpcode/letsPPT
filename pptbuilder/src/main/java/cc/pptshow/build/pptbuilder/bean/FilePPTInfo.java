package cc.pptshow.build.pptbuilder.bean;

import cc.pptshow.build.pptbuilder.util.RandUtil;
import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Data
@NameStyle(Style.camelhump)
@Table(name = "file_ppt_info")
public class FilePPTInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;

    private String channel;

    private String title;

    private Integer dataTitleId;

    private Integer designerId;

    private String colorType;

    private Long colorInfoId;

    private Integer pptStyle;

    private Integer pageSize;

    private String fileFormat = ".pptx";

    private String downUrl;

    private String picLocalUrl;

    private String picUrl;

    private String videoUrl;

    private String aboutText;

    /**
     * 逗号分割的id
     */
    private String tags;

    private String sourceJson;

    private Long homeRegionPutId;

    private Long contentRegionPutId;

    private Long bigTitleRegionPutId;

    private String regionPutList;

    private Integer upload = 0;

    private Date createTime;

}
