package cc.pptshow.build.pptbuilder.domain;

import lombok.Data;

@Data
public class ChannelConfig {

    /**
     * 是否使用共享生成内容
     */
    private boolean useShareFile;

    /**
     * 是否需要生成视频文件
     */
    private boolean needBuildVideo;

    /**
     * 是否需要生成图片
     */
    private boolean needBuildPic;

    /**
     * 是否需要进行图片上传
     * 这里指上传到我们自己的七牛云CDN
     */
    private boolean needUpdatePic;

    /**
     * 是否需要进行视频上传
     * 这里指上传到我们自己的七牛云CDN
     */
    private boolean needUpdateVideo;

    /**
     * 是否需要将生成好的文件进行上传
     * 这里指上传到我们自己的七牛云CDN
     */
    private boolean needUpdateFile;

    /**
     * 图片视频CDN的地址
     * 比如 https://img.mubangou.com
     */
    private String ImgCdnLink;

    /**
     * 如果需要上传七牛云
     * 图片和视频上传到哪个空间里面去
     */
    private String imgBucketName;

    /**
     * 如果需要上传到七牛云
     * PPT文件上传到哪个空间里面去
     */
    private String fileBucketName;

    /**
     * 从哪个时间开始生成
     * 距离当天0点的分钟数
     */
    private int beginBuildMinute = 0;

    /**
     * 生成到什么时间截止
     * 距离当天0点的分钟数
     */
    private int endBuildMinute = 0;

    /**
     * 单日生成最大需求量
     */
    private int maxSizeNeed = 0;

    /**
     * 开始上传的时间
     * 距离当天0点的分钟数
     */
    private int beginUpdateMinute = 0;

    /**
     * 上传到什么时间截止
     * 距离当天0点的分钟数
     */
    private int endUpdateMinute = 0;

    /**
     * 改名文件存储路径，必须 / 结尾
     */
    private String filesPath;

}
