package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 块内样式组成关联表
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "ppt_block_put")
public class PPTBlockPut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 块组合样式id
     */
    private Long pptRegionId;

    /**
     * 对应元素块
     */
    private String pptBlockIds;

}
