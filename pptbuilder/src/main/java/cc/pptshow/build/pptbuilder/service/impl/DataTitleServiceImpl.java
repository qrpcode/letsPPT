package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.DataTitle;
import cc.pptshow.build.pptbuilder.dao.DataTitleMapper;
import cc.pptshow.build.pptbuilder.service.DataTitleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DataTitleServiceImpl implements DataTitleService {

    @Resource
    private DataTitleMapper dataTitleMapper;

    @Override
    public List<DataTitle> findLimit100WithoutAnalysisTitle() {
        return dataTitleMapper.findLimit100WithoutAnalysisTitle();
    }

    @Override
    public DataTitle findById(Long id) {
        return dataTitleMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(DataTitle dataTitle) {
        dataTitleMapper.updateByPrimaryKeySelective(dataTitle);
    }

    @Override
    public List<DataTitle> find100NotSuccess() {
        return dataTitleMapper.find100NotSuccess();
    }

}
