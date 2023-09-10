package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.DataTag;
import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.dao.DataTagMapper;
import cc.pptshow.build.pptbuilder.service.DataTagService;
import cc.pptshow.build.pptbuilder.util.EsUtil;
import cc.pptshow.build.pptbuilder.util.PinUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class DataTagServiceImpl implements DataTagService {

    @Resource
    private DataTagMapper dataTagMapper;

    public DataTag insert(String tag) {
        tag = tag.replace("2021", "2023")
                .replace("2022", "2023")
                .replace("2019", "2023")
                .replace("2018", "2023")
                .replace("2020", "2023");
        List<DataTag> dataTags = dataTagMapper.findByTagName(tag);
        if (CollectionUtils.isEmpty(dataTags)) {
            String pinyin = PinUtil.getPinyin(tag);
            DataTag addTag = new DataTag();
            addTag.setTag(tag);
            getPinYin(addTag, pinyin);
            dataTagMapper.insert(addTag);
            EsUtil.insertTag(addTag);
            return addTag;
        }
        return findMinTag(tag);
    }

    private void getPinYin(DataTag search, String pinyin) {
        pinyin = pinyin.length() > 45 ? pinyin.substring(0, 45) : pinyin;
        String pinyinCopy = pinyin;
        int i = 0;
        while (havePinyinSame(pinyinCopy)) {
            pinyinCopy = pinyin + i;
            i++;
        }
        search.setPinyin(pinyinCopy);
    }

    public void flushNullPinyin() throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        List<DataTag> dataTags = dataTagMapper.selectNullPinyinLimit1000();
        while (!CollectionUtils.isEmpty(dataTags)) {

            for (int i = 0; i < dataTags.size(); i = i + 10) {
                final CountDownLatch cdl = new CountDownLatch(10);
                for (int j = 0; j < 10; j++) {
                    int finalI = i;
                    int finalJ = j;
                    List<DataTag> finalDataTags = dataTags;
                    if (dataTags.size() > i + j) {
                        threadPool.submit(new Runnable() {
                            @Override
                            public synchronized void run() {
                                flushHistoryTag(finalDataTags.get(finalI + finalJ));
                                cdl.countDown();
                            }
                        });
                    }
                }
                cdl.await();
            }
            dataTags = dataTagMapper.selectNullPinyinLimit1000();
        }
    }

    private void flushHistoryTag(DataTag dataTag) {
        dataTag.setTag(dataTag.getTag().replace("2021", "2023")
                .replace("2022", "2023")
                .replace("2019", "2023")
                .replace("2018", "2023")
                .replace("2020", "2023"));
        String pinyin = PinUtil.getPinyin(dataTag.getTag());
        getPinYin(dataTag, pinyin);
        dataTagMapper.updateByPrimaryKey(dataTag);
    }

    private boolean havePinyinSame(String pinyinCopy) {
        DataTag search = new DataTag();
        search.setPinyin(pinyinCopy);
        return !CollectionUtils.isEmpty(dataTagMapper.select(search));
    }

    public void deleteSameWord() {
        for (int i = 0; i < 455302; i++) {
            log.info("[delete] now id : {}", i);
            DataTag dataTag = dataTagMapper.selectByPrimaryKey(i);
            if (Objects.nonNull(dataTag)) {
                DataTag same = dataTagMapper.selectSameWordAndIdMin(dataTag.getTag(), dataTag.getId());
                if (Objects.nonNull(same) || Strings.isEmpty(dataTag.getTag())) {
                    dataTagMapper.delete(dataTag);
                }
            }
        }
    }

    @Override
    public List<DataTag> findByBegin(int beginId) {
        return dataTagMapper.findByBegin(beginId);
    }

    @Override
    public List<DataTag> selectByName(String tag) {
        return dataTagMapper.findByTagName(tag);
    }

    @Override
    public void delete(DataTag dataTag) {
        dataTagMapper.delete(dataTag);
    }

    @Override
    public DataTag findMinTag(String item) {
        return dataTagMapper.findMinTag(item);
    }

}
