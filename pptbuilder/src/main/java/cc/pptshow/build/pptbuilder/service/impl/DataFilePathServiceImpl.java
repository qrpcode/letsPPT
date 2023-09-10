package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.DataFilePath;
import cc.pptshow.build.pptbuilder.dao.DataFilePathMapper;
import cc.pptshow.build.pptbuilder.service.DataFilePathService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class DataFilePathServiceImpl implements DataFilePathService {

    @Resource
    private DataFilePathMapper dataFilePathMapper;

    private static final Set<String> HAVE_SET = Sets.newHashSet();

    @Override
    public void findAllFileByPath(String path) {
        File file = new File(path);
        findAllPptxByFile(file);
    }

    @Override
    public void abandonPPTFile(String path) {
        markFileByStatus(path, -2);
    }

    @Override
    public void markPPTFile(String path) {
        markFileByStatus(path, 2);
    }

    private void markFileByStatus(String path, int status) {
        DataFilePath search = new DataFilePath();
        search.setPath(path);
        List<DataFilePath> select = dataFilePathMapper.select(search);
        for (DataFilePath dataFilePath : select) {
            dataFilePath.setNowPage(status);
            dataFilePathMapper.updateByPrimaryKey(dataFilePath);
        }
    }

    @Override
    public String getLastUnmark() {
        DataFilePath dataFilePath = dataFilePathMapper.selectLastUnmark();
        return dataFilePath.getPath();
    }

    @Override
    public List<DataFilePath> findByPath(String filePath) {
        DataFilePath search = new DataFilePath();
        search.setPath(filePath);
        return dataFilePathMapper.select(search);
    }

    @Override
    public void update(DataFilePath path) {
        dataFilePathMapper.updateByPrimaryKey(path);
    }

    @Override
    public DataFilePath findLastNoImgPath() {
        return dataFilePathMapper.findLastNoImgPath();
    }

    private void findAllPptxByFile(File file) {
        if (Objects.isNull(file) || Objects.isNull(file.listFiles())) {
            return;
        }
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            if (listFile.isFile()) {
                if (listFile.getPath().endsWith(".pptx")) {
                    if (HAVE_SET.add(listFile.getPath())) {
                        DataFilePath dataFilePath = new DataFilePath();
                        dataFilePath.setPath(listFile.getPath());
                        List<DataFilePath> filePaths = dataFilePathMapper.select(dataFilePath);
                        if (CollectionUtils.isEmpty(filePaths)) {
                            dataFilePathMapper.insertSelective(dataFilePath);
                        }
                    }
                }
            } else {
                findAllPptxByFile(listFile);
            }
        }
    }

}
