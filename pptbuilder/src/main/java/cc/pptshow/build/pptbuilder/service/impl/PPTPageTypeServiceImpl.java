package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTPageType;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.dao.PPTPageTypeMapper;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.service.PPTPageTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PPTPageTypeServiceImpl implements PPTPageTypeService {

    @Resource
    private PPTPageTypeMapper pptPageTypeMapper;

    @Override
    public List<PPTPageType> findAll() {
        return pptPageTypeMapper.selectAll();
    }

    @Override
    public PPTPageType findByEnName(String enName) {
        PPTPageType search = new PPTPageType();
        search.setPageTypeNameEn(enName);
        return pptPageTypeMapper.select(search).stream().findFirst().orElse(null);
    }

    @Override
    public PPTPageType findById(int id) {
        return pptPageTypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Integer> findTextMaybeCenterPageIds() {
        PPTPageType search = new PPTPageType();
        search.setMaybeTextCenter(1);
        List<PPTPageType> pageTypes = pptPageTypeMapper.select(search);
        return pageTypes.stream().map(PPTPageType::getId).collect(Collectors.toList());
    }

    @Override
    public PPTPage findPPTPageById(Integer id) {
        if (id.equals(BConstant.HOME_PAGE_ID)) {
            return PPTPage.HOME;
        } else if (id.equals(BConstant.TITLE_PAGE_ID)) {
            return PPTPage.INNER_TITLE;
        } else if (id.equals(BConstant.BIG_TITLE_PAGE_ID)) {
            return PPTPage.BIG_TITLE;
        } else {
            return PPTPage.OTHER;
        }
    }

    @Override
    public List<PPTPageType> findAllCanPutTitlePage() {
        PPTPageType pptPageType = new PPTPageType();
        pptPageType.setNeedTitle(1);
        return pptPageTypeMapper.select(pptPageType);
    }

    public Integer findByName(String name) {
        PPTPageType pptPageType = new PPTPageType();
        pptPageType.setPageTypeNameEn(name);
        return pptPageTypeMapper.select(pptPageType).stream().findFirst().map(PPTPageType::getId).orElse(null);
    }

    @Override
    public Integer findHomeNumber() {
        return findByName("home");
    }

    @Override
    public Integer findContentsNumber() {
        return findByName("contents");
    }

    @Override
    public Integer findIntroductionNumber() {
        return findByName("introduction");
    }

    @Override
    public Integer findThankNumber() {
        return findByName("thank");
    }

    @Override
    public Integer findBigTitleNumber() {
        return findByName("bigTitle");
    }
    @Override
    public boolean isPageNeedTitle(Integer pageId) {
        PPTPageType pptPageType = findById(pageId);
        return pptPageType.getNeedTitle() == 1;
    }

}
