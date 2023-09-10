package cc.pptshow.build.pptbuilder.biz.builder.impl;

import cc.pptshow.build.pptbuilder.bean.DataTag;
import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.biz.analysis.TitleAnalysis;
import cc.pptshow.build.pptbuilder.biz.builder.PPTRemakeBiz;
import cc.pptshow.build.pptbuilder.biz.builder.TitleBuilder;
import cc.pptshow.build.pptbuilder.biz.builder.helper.MediaHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import cc.pptshow.build.pptbuilder.domain.vo.NlpItemVo;
import cc.pptshow.build.pptbuilder.domain.vo.NlpVo;
import cc.pptshow.build.pptbuilder.service.DataTagService;
import cc.pptshow.build.pptbuilder.service.FilePPTInfoService;
import cc.pptshow.build.pptbuilder.service.PPTRegionPutService;
import cc.pptshow.build.pptbuilder.util.EsUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PPTRemakeBizImpl implements PPTRemakeBiz {

    @Resource
    private FilePPTInfoService filePPTInfoService;

    @Resource
    private MediaHelper mediaHelper;

    @Resource
    private TitleBuilder titleBuilder;

    @Resource
    private PPTRegionPutService pptRegionPutService;

    @Resource
    private TitleAnalysis titleAnalysis;

    @Resource
    private DataTagService dataTagService;

    @Override
    public void deleteAndFlush(String uid) {
        FilePPTInfo filePPTInfo = filePPTInfoService.selectByUid(uid);
        String title = filePPTInfo.getTitle();
        filePPTInfo.setTitle(title + "-1");
        filePPTInfoService.updateById(filePPTInfo);
        try {
            flushPPT(filePPTInfo);
            deleteOld(filePPTInfo);
        } catch (Throwable t) {
            filePPTInfo.setTitle(title);
            filePPTInfoService.updateById(filePPTInfo);
        }
    }

    @Override
    public void delete(String uid) {
        FilePPTInfo filePPTInfo = filePPTInfoService.selectByUid(uid);
        deleteOld(filePPTInfo);
    }

    @Override
    public void addHomeBlackList(String uid) {
        FilePPTInfo filePPTInfo = filePPTInfoService.selectByUid(uid);
        addBlackList(filePPTInfo.getHomeRegionPutId());
    }

    @Override
    public void addContentBlackList(String uid) {
        FilePPTInfo filePPTInfo = filePPTInfoService.selectByUid(uid);
        if (Objects.nonNull(filePPTInfo.getContentRegionPutId())) {
            addBlackList(filePPTInfo.getContentRegionPutId());
        }
    }

    @Override
    public void addBlackByUidAndPageNumber(String uid, String pageNumber) {
        FilePPTInfo filePPTInfo = filePPTInfoService.selectByUid(uid);
        String regionPutList = filePPTInfo.getRegionPutList();
        JSONObject jsonObject = JSON.parseObject(regionPutList);
        String regionPutId = jsonObject.get(pageNumber).toString();
        log.warn("[当前正在拉黑RegionPut单元] 拉黑id:{}", regionPutId);
        addBlackList(Long.parseLong(regionPutId));
    }

    private void addBlackList(Long id) {
        if (Objects.nonNull(id)) {
            PPTRegionPut pptRegionPut = pptRegionPutService.selectById(id);
            if (pptRegionPut.getPptPageId() < 1000) {
                pptRegionPut.setPptPageId(pptRegionPut.getPptPageId() + 1000);
                pptRegionPutService.update(pptRegionPut);
            }
        }
    }

    @Override
    public void addBigTitleBlackList(String uid) {
        FilePPTInfo filePPTInfo = filePPTInfoService.selectByUid(uid);
        addBlackList(filePPTInfo.getBigTitleRegionPutId());
    }

    private void flushPPT(FilePPTInfo filePPTInfo) {
        if (Objects.nonNull(filePPTInfo.getDataTitleId())) {
            FilePPTInfo newInfo = titleBuilder.buildByTitleId((long) filePPTInfo.getDataTitleId(), ChannelType.MBG);
            if (Objects.isNull(newInfo)) {
                throw new RuntimeException("生成错误");
            } else {
                filePPTInfoService.delete(filePPTInfo);
            }
            newInfo.setUid(filePPTInfo.getUid());
            filePPTInfoService.updateById(newInfo);
            EsUtil.insertPPT(newInfo);
        } else {
            throw new RuntimeException("没有找到原始Title信息");
        }
    }

    private void deleteOld(FilePPTInfo filePPTInfo) {
        mediaHelper.deleteFile(filePPTInfo.getDownUrl());
        mediaHelper.deleteImg(filePPTInfo.getPicUrl());
        mediaHelper.deleteImg(filePPTInfo.getVideoUrl());
    }

    public void flushTagNlp() {
        List<FilePPTInfo> pptInfos = filePPTInfoService.findAll();
        for (FilePPTInfo pptInfo : pptInfos) {
            String title = pptInfo.getTitle();
            title = title.replace("PPT模板", "");
            log.info("[id:{}] {}", pptInfo.getId(), title);
            NlpVo nlpVo = titleAnalysis.nlpText(title);
            if (Objects.isNull(nlpVo)) {
                log.error("[NLP失效] {}", title);
            }
            log.info("NLP:{}", nlpVo);
            List<Integer> tagIds = Lists.newArrayList();
            for (NlpItemVo item : nlpVo.getItems()) {
                DataTag dataTag = dataTagService.insert(item.getItem());
                tagIds.add(dataTag.getId());
            }
            pptInfo.setTags(BConstant.JOINER.join(tagIds));
            filePPTInfoService.updateById(pptInfo);
        }
    }
}
