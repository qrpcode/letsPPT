package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;

import java.util.List;

public interface FilePPTInfoService {

    void insertFilePPTInfo(FilePPTInfo filePPTInfo);

    String getUid(GlobalStyle globalStyle);

    List<FilePPTInfo> findByBegin(int beginId);

    FilePPTInfo selectByUid(String uid);

    void updateById(FilePPTInfo filePPTInfo);

    void delete(FilePPTInfo filePPTInfo);

    boolean haveSameTitle(String title);

    List<FilePPTInfo> findAll();

    List<FilePPTInfo> selectGreatByNumber(int count);

    List<FilePPTInfo> selectNormalByNumber(int count);

    long findNotUpdateCount();

    List<FilePPTInfo> selectWaitByNumber();

}
