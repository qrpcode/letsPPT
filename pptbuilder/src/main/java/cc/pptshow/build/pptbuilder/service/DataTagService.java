package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.DataTag;
import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;

import java.util.List;

public interface DataTagService {

    DataTag insert(String tag);

    void flushNullPinyin() throws InterruptedException;

    void deleteSameWord();

    List<DataTag> findByBegin(int beginId);

    List<DataTag> selectByName(String tag);

    void delete(DataTag dataTag);

    DataTag findMinTag(String item);

}
