package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.dao.FilePPTInfoMapper;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.service.FilePPTInfoService;
import cc.pptshow.build.pptbuilder.util.EsUtil;
import cn.hutool.core.date.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class FilePPTInfoServiceImpl implements FilePPTInfoService {

    @Resource
    private FilePPTInfoMapper filePPTInfoMapper;

    @Override
    public void insertFilePPTInfo(FilePPTInfo filePPTInfo) {
        filePPTInfoMapper.insertSelective(filePPTInfo);
    }

    @Override
    public String getUid(GlobalStyle globalStyle) {
        String date = DateUtil.format(new Date(), "yyyyMMddHH");
        int count = filePPTInfoMapper.selectCountByDate(date) + 1;
        String longNumber = "000000000" + count;
        return date + longNumber.substring(longNumber.length() - 4);
    }

    @Override
    public List<FilePPTInfo> findByBegin(int begin) {
        return filePPTInfoMapper.findByBegin(begin);
    }

    @Override
    public FilePPTInfo selectByUid(String uid) {
        FilePPTInfo search = new FilePPTInfo();
        search.setUid(uid);
        return filePPTInfoMapper.selectOne(search);
    }

    @Override
    public void updateById(FilePPTInfo filePPTInfo) {
        filePPTInfoMapper.updateByPrimaryKey(filePPTInfo);
    }

    @Override
    public void delete(FilePPTInfo filePPTInfo) {
        filePPTInfoMapper.delete(filePPTInfo);
    }

    @Override
    public boolean haveSameTitle(String title) {
        FilePPTInfo search = new FilePPTInfo();
        search.setTitle(title);
        List<FilePPTInfo> select = filePPTInfoMapper.select(search);
        return !CollectionUtils.isEmpty(select);
    }

    @Override
    public List<FilePPTInfo> findAll() {
        return filePPTInfoMapper.selectAll();
    }

    @Override
    public List<FilePPTInfo> selectGreatByNumber(int count) {
        return filePPTInfoMapper.selectGreatByCount(count);
    }

    @Override
    public List<FilePPTInfo> selectNormalByNumber(int count) {
        return filePPTInfoMapper.selectNormalByCount(count);
    }

    @Override
    public long findNotUpdateCount() {
        return filePPTInfoMapper.findNotUpdateCount();
    }

    @Override
    public List<FilePPTInfo> selectWaitByNumber() {
        return filePPTInfoMapper.selectWait();
    }

}
