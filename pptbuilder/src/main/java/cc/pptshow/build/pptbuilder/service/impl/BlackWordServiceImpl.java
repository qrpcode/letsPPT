package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.BlackWord;
import cc.pptshow.build.pptbuilder.dao.BlackWordMapper;
import cc.pptshow.build.pptbuilder.service.BlackWordService;
import cc.pptshow.build.pptbuilder.util.Safes;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlackWordServiceImpl implements BlackWordService {

    @Resource
    private BlackWordMapper blackWordMapper;

    @Override
    public List<String> findAllBlackWords() {
        return Safes.of(blackWordMapper.selectAll())
                .stream()
                .map(BlackWord::getBlackWord)
                .collect(Collectors.toList());
    }

}
