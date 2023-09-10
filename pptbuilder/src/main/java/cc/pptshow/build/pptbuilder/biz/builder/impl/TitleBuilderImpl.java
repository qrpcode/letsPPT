package cc.pptshow.build.pptbuilder.biz.builder.impl;

import cc.pptshow.build.pptbuilder.bean.DataTitle;
import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.biz.analysis.TitleAnalysis;
import cc.pptshow.build.pptbuilder.biz.builder.PPTBuildBiz;
import cc.pptshow.build.pptbuilder.biz.builder.TitleBuilder;
import cc.pptshow.build.pptbuilder.biz.builder.helper.MediaHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.BuildRequire;
import cc.pptshow.build.pptbuilder.domain.ChannelConfig;
import cc.pptshow.build.pptbuilder.domain.StyleAnalysis;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import cc.pptshow.build.pptbuilder.service.BlackWordService;
import cc.pptshow.build.pptbuilder.service.DataTitleService;
import cc.pptshow.build.pptbuilder.service.FilePPTInfoService;
import cc.pptshow.build.pptbuilder.util.EsUtil;
import cc.pptshow.build.pptbuilder.util.PathUtil;
import cc.pptshow.ppt.constant.Constant;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TitleBuilderImpl implements TitleBuilder {

    @Resource
    private PPTBuildBiz pptBuildBiz;

    @Resource
    private DataTitleService dataTitleService;

    @Resource
    private TitleAnalysis titleAnalysis;

    @Resource
    private FilePPTInfoService filePPTInfoService;

    @Resource
    private MediaHelper mediaHelper;

    @Resource
    private BlackWordService blackWordService;

    @Override
    public FilePPTInfo buildByTitleId(Long id, ChannelType channelType) {
        try {
            StyleAnalysis styleAnalysis = titleAnalysis.analysisTitle(id);
            BuildRequire buildRequire = new BuildRequire();
            buildRequire.setChannelType(channelType);
            buildRequire.setDataTitleId(Integer.parseInt(Long.toString(id)));
            String title = styleAnalysis.getNewTitles().get(0);
            buildRequire.setTitle(title);
            if (matchBlackWord(title)) {
                DataTitle dataTitle = dataTitleService.findById(id);
                dataTitle.setBuildSuccess(-2);
                dataTitleService.update(dataTitle);
                log.error("命中文本黑名单，不允许生成");
                return null;
            } else {
                FilePPTInfo filePPTInfo = pptBuildBiz.buildByRequire(buildRequire);
                DataTitle dataTitle = dataTitleService.findById(id);
                dataTitle.setBuildSuccess(1);
                dataTitleService.update(dataTitle);
                return filePPTInfo;
            }
        } catch (Exception t) {
            DataTitle dataTitle = dataTitleService.findById(id);
            dataTitle.setBuildSuccess(-1);
            dataTitleService.update(dataTitle);
            log.error("构建失败", t);
            //throw t;
        }
        return null;
    }

    private boolean matchBlackWord(String title) {
        return blackWordService.findAllBlackWords().stream().anyMatch(title::contains);
    }

    @Override
    public void buildAll() {
        List<DataTitle> dataTitles = dataTitleService.find100NotSuccess();
        while (!CollectionUtils.isEmpty(dataTitles)) {
            for (DataTitle dataTitle : dataTitles) {
                FilePPTInfo filePPTInfo = buildByTitleId(dataTitle.getId(), ChannelType.MBG);
                EsUtil.insertPPT(filePPTInfo);
            }
            dataTitles = dataTitleService.find100NotSuccess();
        }
    }

    @Override
    @Scheduled(cron = "0 0/20 * * * ?")
    public void buildTodayGlobal() {
        List<DataTitle> dataTitles = dataTitleService.find100NotSuccess();
        if (CollectionUtils.isEmpty(dataTitles)) {
            return;
        }
        DataTitle dataTitle = dataTitles.get(0);
        buildByTitleId(dataTitle.getId(), ChannelType.GLOBAL);
    }

    /**
     * 把所有标注的分类处理掉
     * 其中：
     * 优质的：
     *    找出300个上传模板狗
     *    找出其中50个上传百度文库
     *    找出其中201个上传原创力文库
     * 普通的：
     *    找出其中600个单独放在一个文件夹，上传到道客巴巴和人人文库
     *
     * 剩下的就不处理
     */
    @Override
    public void buildByRemark() {
        List<FilePPTInfo> mbgPPTs = filePPTInfoService.selectGreatByNumber(150);
        updateRemarkByMbg(mbgPPTs);
        List<FilePPTInfo> kuaiPPTs = filePPTInfoService.selectGreatByNumber(150);
        updateRemarkByKuai(kuaiPPTs);
        //List<FilePPTInfo> bdPPTs = filePPTInfoService.selectGreatByNumber(200);
        //moveInTodayBd(bdPPTs);
        //List<FilePPTInfo> maxPPTs = filePPTInfoService.selectGreatByNumber(201);
        //moveInToday(filePPTInfos);
        //List<FilePPTInfo> normalPPTs = filePPTInfoService.selectNormalByNumber(600);
        //moveInTodayNormal(normalPPTs);

        List<FilePPTInfo> waitPPTs = filePPTInfoService.selectWaitByNumber();
        mbgPPTs = filterInWait(waitPPTs, ChannelType.MBG);
        kuaiPPTs = filterInWait(waitPPTs, ChannelType.KUAI);
        updateByChannel(mbgPPTs, ChannelType.MBG);
        updateByChannel(kuaiPPTs, ChannelType.KUAI);
    }

    @NotNull
    private List<FilePPTInfo> filterInWait(List<FilePPTInfo> waitPPTs, ChannelType kuai) {
        return waitPPTs.stream()
                .filter(p -> p.getChannel().equals(kuai.getCode()))
                .collect(Collectors.toList());
    }

    private void updateRemarkByKuai(List<FilePPTInfo> kuaiPPTs) {
        for (FilePPTInfo ppt : kuaiPPTs) {
            ppt.setUpload(-1001);
            ppt.setChannel(ChannelType.KUAI.getCode());
            filePPTInfoService.updateById(ppt);
        }
    }

    private void updateRemarkByMbg(List<FilePPTInfo> mbgPPTs) {
        for (FilePPTInfo mbgPPT : mbgPPTs) {
            mbgPPT.setUpload(-1001);
            mbgPPT.setChannel(ChannelType.MBG.getCode());
            filePPTInfoService.updateById(mbgPPT);
        }
    }

    private void moveInTodayNormal(List<FilePPTInfo> normalPPTs) {
        String path = PathUtil.buildTodayPath() + Constant.SEPARATOR + "第三方文库";
        new File(path).mkdirs();
        for (FilePPTInfo info : normalPPTs) {
            info.setUpload(1001);
            filePPTInfoService.updateById(info);
            FileUtil.copy(info.getDownUrl(), path + Constant.SEPARATOR + info.getTitle() + ".pptx", true);
        }
    }

    private void moveInTodayBd(List<FilePPTInfo> bdPPTs) {
        String path = PathUtil.buildTodayPath() + Constant.SEPARATOR + "百度文库";
        new File(path).mkdirs();
        for (FilePPTInfo bdPPT : bdPPTs) {
            bdPPT.setUpload(1001);
            filePPTInfoService.updateById(bdPPT);
            FileUtil.copy(bdPPT.getDownUrl(), path + Constant.SEPARATOR + bdPPT.getTitle() + ".pptx", true);
        }
    }

    private void updateByChannel(List<FilePPTInfo> ppts, ChannelType channelType) {
        ChannelConfig channelConfig = BConstant.config.get(channelType.getCode());
        for (FilePPTInfo ppt : ppts) {
            ppt.setChannel(channelType.getCode());
            if (channelConfig.isNeedBuildVideo()) {
                ppt.setVideoUrl(mediaHelper.ppt2MP4(ppt.getDownUrl()));
            }
            if (channelConfig.isNeedUpdatePic()) {
                ppt.setPicUrl(mediaHelper.upload(channelConfig.getImgBucketName(),
                        channelConfig.getImgCdnLink(), ppt.getPicLocalUrl()));
            }
            if (channelConfig.isNeedUpdateVideo()) {
                assert Strings.isNotEmpty(channelConfig.getImgBucketName());
                ppt.setVideoUrl(mediaHelper.upload(channelConfig.getImgBucketName(),
                        channelConfig.getImgCdnLink(), ppt.getVideoUrl()));
            }
            if (channelConfig.isNeedUpdateFile()) {
                ppt.setDownUrl(mediaHelper.updatePPT(ppt.getDownUrl()));
            }
            ppt.setUpload(1001);
            filePPTInfoService.updateById(ppt);
            EsUtil.insertPPT(ppt);
        }
    }

}
