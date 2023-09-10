package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTPageModel;
import cc.pptshow.build.pptbuilder.bean.PPTPageType;
import cc.pptshow.build.pptbuilder.dao.PPTPageModelMapper;
import cc.pptshow.build.pptbuilder.service.PPTPageModelService;
import cc.pptshow.build.pptbuilder.service.PPTPageTypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class PPTPageModelServiceImpl implements PPTPageModelService {

    @Resource
    private PPTPageModelMapper pptPageModelMapper;

    @Resource
    private PPTPageTypeService pptPageTypeService;

    @Override
    public List<PPTPageModel> findAll() {
        return pptPageModelMapper.selectAll();
    }

    @Override
    public Integer findByPageAndModelName(String pageName, String modelName) {
        PPTPageType pptPageType = pptPageTypeService.findAll().stream()
                .filter(p -> p.getPageTypeName().equals(pageName))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(pptPageType)) {
            return null;
        }
        PPTPageModel search = new PPTPageModel();
        search.setPptPageTypeId(pptPageType.getId());
        search.setPptPageModelName(modelName);
        List<PPTPageModel> select = pptPageModelMapper.select(search);
        if (CollectionUtils.isEmpty(select)) {
            return null;
        }
        return select.get(0).getId();
    }

    @Override
    public List<PPTPageModel> findByPageId(Integer id) {
        PPTPageModel search = new PPTPageModel();
        search.setPptPageTypeId(id);
        return pptPageModelMapper.select(search);
    }

    @Override
    public PPTPageModel findById(Integer id) {
        return pptPageModelMapper.selectByPrimaryKey(id);
    }

}
