package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTPageType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;

import java.util.List;

public interface PPTPageTypeService {

    List<PPTPageType> findAll();

    PPTPageType findByEnName(String enName);

    PPTPageType findById(int id);

    List<Integer> findTextMaybeCenterPageIds();

    PPTPage findPPTPageById(Integer id);

    List<PPTPageType> findAllCanPutTitlePage();

    Integer findHomeNumber();

    Integer findContentsNumber();

    Integer findIntroductionNumber();

    Integer findThankNumber();

    Integer findBigTitleNumber();

    boolean isPageNeedTitle(Integer pageId);

}
