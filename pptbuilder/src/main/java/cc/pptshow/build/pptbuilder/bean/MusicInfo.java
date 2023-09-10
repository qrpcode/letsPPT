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
@Table(name = "music_info")
public class MusicInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 音乐路径
     */
    private String musicPath;

    /**
     * 风格标签
     */
    private int pptStyleId;

    /**
     * 音乐名称
     */
    private String musicName;

    /**
     * 音乐来源机构
     */
    private String musicFrom;

    /**
     * 来源地址
     */
    private String fromUrl;

    /**
     * 授权许可（一般是CC0）
     */
    private String license;

}
