package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.DataFilePath;

import java.util.List;

public interface DataFilePathService {

    void findAllFileByPath(String path);

    void abandonPPTFile(String path);

    void markPPTFile(String path);

    String getLastUnmark();

    List<DataFilePath> findByPath(String filePath);

    void update(DataFilePath path);

    DataFilePath findLastNoImgPath();

}
