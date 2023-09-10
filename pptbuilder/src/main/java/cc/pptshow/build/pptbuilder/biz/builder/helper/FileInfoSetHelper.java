package cc.pptshow.build.pptbuilder.biz.builder.helper;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.ChannelConfig;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileInfoSetHelper {

    @Resource
    private TagHelper tagHelper;

    @Resource
    private AboutBuildHelper aboutBuildHelper;

    @Resource
    private MediaHelper mediaHelper;

    /**
     * 没有 aboutText、video、pic、down信息
     */
    @SneakyThrows
    public FilePPTInfo buildMainField(String pptPath,
                                      GlobalStyle globalStyle,
                                      Integer dataTitleId,
                                      ChannelConfig channelConfig) {
        FilePPTInfo filePPTInfo = new FilePPTInfo();
        filePPTInfo.setAboutText(aboutBuildHelper.buildByStyle(globalStyle));
        if (channelConfig.isNeedBuildVideo()) {
            filePPTInfo.setVideoUrl(mediaHelper.ppt2MP4(pptPath));
        }
        Thread.sleep(1000);
        if (channelConfig.isNeedBuildPic()) {
            String picLocalUrl = mediaHelper.ppt2Png(pptPath, globalStyle.getChannelType());
            filePPTInfo.setPicLocalUrl(picLocalUrl);
        }
        if (channelConfig.isNeedUpdatePic()) {
            filePPTInfo.setPicUrl(mediaHelper.upload(channelConfig.getImgBucketName(),
                    channelConfig.getImgCdnLink(), filePPTInfo.getPicLocalUrl()));
        }
        filePPTInfo.setSourceJson(JSON.toJSONString(globalStyle.getCopyrightFromList()));
        filePPTInfo.setPptStyle(globalStyle.getPptStyle().getId());
        filePPTInfo.setPageSize(globalStyle.getPptPageIds().size());
        filePPTInfo.setColorType(globalStyle.getColorInfo().getColorType());
        filePPTInfo.setColorInfoId(globalStyle.getColorInfo().getId());
        filePPTInfo.setChannel(globalStyle.getChannelType().getCode());
        filePPTInfo.setTitle(globalStyle.getTitle());
        filePPTInfo.setDataTitleId(dataTitleId);
        filePPTInfo.setUid(globalStyle.getUuid());
        filePPTInfo.setDesignerId(RandUtil.round(1, 50));
        if (channelConfig.isNeedBuildVideo()) {
            filePPTInfo.setVideoUrl(mediaHelper.mp4Compression(filePPTInfo.getVideoUrl()));
        }
        filePPTInfo.setTags(tagHelper.saveTagsToIds(globalStyle.getNlpVo().getItems()));
        if (channelConfig.isNeedUpdateVideo()) {
            assert Strings.isNotEmpty(channelConfig.getImgBucketName());
            filePPTInfo.setVideoUrl(mediaHelper.upload(channelConfig.getImgBucketName(),
                    channelConfig.getImgCdnLink(), filePPTInfo.getVideoUrl()));
        }
        if (channelConfig.isNeedUpdateFile()) {
            filePPTInfo.setDownUrl(mediaHelper.updatePPT(pptPath));
        } else {
            filePPTInfo.setDownUrl(pptPath);
        }
        filePPTInfo.setHomeRegionPutId(globalStyle.getRegionPuts().get(0).getId());
        filePPTInfo.setContentRegionPutId(globalStyle.getRegionPuts().get(1).getId());
        filePPTInfo.setBigTitleRegionPutId(globalStyle.getRegionPuts().get(2).getId());
        filePPTInfo.setRegionPutList(JSON.toJSONString(getPageNumberAndIdMap(globalStyle.getRegionPuts())));
        log.info("[生成成功] filePPTInfo: {}", filePPTInfo);
        return filePPTInfo;
    }

    private Map<String, Long> getPageNumberAndIdMap(Map<Integer, PPTRegionPut> regionPuts) {
        return regionPuts.entrySet().stream()
                .collect(Collectors.toMap(e -> Integer.toString(e.getKey()), e -> e.getValue().getId()));
    }


}
