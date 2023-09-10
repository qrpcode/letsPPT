package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.ConvertPptPng;

public interface ConvertPPTPngService {

    ConvertPptPng signLastNotConvert();

    void updateFinish(Long id);

    void insert(ConvertPptPng pptPng);

    ConvertPptPng selectById(long id);

}
