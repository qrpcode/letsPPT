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
@Table(name = "ppt_block_line")
public class PPTBlockLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long blockId;

    private String lineType;

    private Double lineWidth;

}
