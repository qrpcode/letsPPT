package cc.pptshow.build.pptbuilder.biz.builder;

/**
 * PPT重新制作
 * 比如原来的人工识别为废稿，新的就需要重新制作
 */
public interface PPTRemakeBiz {

    void deleteAndFlush(String uid);

    void delete(String uid);

    void addHomeBlackList(String uid);

    void addBigTitleBlackList(String uid);

    void addContentBlackList(String uid);

    void addBlackByUidAndPageNumber(String uid, String pageNumber);

    void flushTagNlp();

}
