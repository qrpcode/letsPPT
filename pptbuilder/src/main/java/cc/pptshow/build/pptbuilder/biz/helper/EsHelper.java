package cc.pptshow.build.pptbuilder.biz.helper;

import cc.pptshow.build.pptbuilder.bean.DataTag;
import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import cc.pptshow.build.pptbuilder.service.DataTagService;
import cc.pptshow.build.pptbuilder.service.FilePPTInfoService;
import cc.pptshow.build.pptbuilder.util.EsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
public class EsHelper {

    @Resource
    private FilePPTInfoService filePPTInfoService;

    @Resource
    private DataTagService dataTagService;

    public void flushAllPPT() {
        int beginId = 1000;
        List<FilePPTInfo> filePPTInfos = filePPTInfoService.findByBegin(beginId);
        while (!CollectionUtils.isEmpty(filePPTInfos)) {
            for (FilePPTInfo filePPTInfo : filePPTInfos) {
                if (ChannelType.MBG.getCode().equals(filePPTInfo.getChannel())) {
                    EsUtil.insertPPT(filePPTInfo);
                }
            }
            beginId += 1000;
            filePPTInfos = filePPTInfoService.findByBegin(beginId);
        }
    }

    public void flushAllTag() throws InterruptedException {
        int beginId = 100000;
        List<DataTag> dataTags = dataTagService.findByBegin(beginId);
        while (!CollectionUtils.isEmpty(dataTags)) {
            for (int i = 0; i < dataTags.size(); i = i + 2) {
                final CountDownLatch cdl = new CountDownLatch(2);
                for (int j = 0; j < 2; j++) {
                    DataTag dataTag = dataTags.get(i + j);
                    BConstant.threadPool.submit(new Runnable() {
                        @Override
                        public synchronized void run() {
                            //检查是否存在同名
                            List<DataTag> tags = dataTagService.selectByName(dataTag.getTag());
                            Integer minId = findMinId(tags);
                            if (minId.equals(dataTag.getId())) {
                                EsUtil.insertTag(dataTag);
                                if (tags.size() > 1) {
                                    tags.remove(dataTag);
                                    for (DataTag tag : tags) {
                                        log.error("【Tag删除】{} 和 {} 重复，内容都是: {}", tag.getId(), dataTag.getId(), dataTag.getTag());
                                        EsUtil.deleteTag(tag.getId());
                                        dataTagService.delete(tag);
                                    }
                                }
                            } else {
                                log.error("【Tag删除】{} 和 {} 重复，内容都是: {}", minId, dataTag.getId(), dataTag.getTag());
                                EsUtil.deleteTag(dataTag.getId());
                                dataTagService.delete(dataTag);
                            }
                            cdl.countDown();
                        }
                    });
                }
                cdl.await();
            }
            beginId += 1000;
            log.info("[ES刷Tag] 新的一组");
            dataTags = dataTagService.findByBegin(beginId);
        }
    }

    public Integer findMinId(List<DataTag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return 0;
        }
        Integer min = null;
        for (DataTag tag : tags) {
            if (Objects.isNull(min) || tag.getId() < min) {
                min = tag.getId();
            }
        }
        return min;
    }

}
