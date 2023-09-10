package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.MusicInfo;
import cc.pptshow.build.pptbuilder.dao.MusicInfoMapper;
import cc.pptshow.build.pptbuilder.service.MusicInfoService;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class MusicInfoServiceImpl implements MusicInfoService {

    @Resource
    private MusicInfoMapper musicInfoMapper;

    @Override
    public MusicInfo findRandOneByStyle(Integer id) {
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.setPptStyleId(id);
        List<MusicInfo> infos = musicInfoMapper.select(musicInfo);
        return RandUtil.round(infos);
    }
}
