package cc.pptshow.build.pptbuilder.biz.read.impl;

import cc.pptshow.build.pptbuilder.bean.DataFilePath;
import cc.pptshow.build.pptbuilder.biz.read.ReadBiz;
import cc.pptshow.build.pptbuilder.domain.PPTPageRead;
import cc.pptshow.build.pptbuilder.domain.ReadElement;
import cc.pptshow.build.pptbuilder.library.OfficeLibrary;
import cc.pptshow.build.pptbuilder.service.DataFilePathService;
import cc.pptshow.build.pptbuilder.util.PathUtil;
import cc.pptshow.build.pptbuilder.util.PositionUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.reader.PPTSlideRead;
import cc.pptshow.ppt.util.PPT2ImgUtil;
import cc.pptshow.ppt.util.PPTUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static cc.pptshow.ppt.constant.Constant.SEPARATOR;

@Slf4j
@Service
public class ReadBizImpl implements ReadBiz {

    @Resource
    private DataFilePathService dataFilePathService;

    @Resource
    private OfficeLibrary officeLibrary;

    @Override
    public List<List<PPTElement>> readPPT(String filePath) {
        String pptPath = unzipPPT(filePath);
        try {
            List<List<PPTElement>> allPageElements = Lists.newArrayList();
            List<String> xmlList = PathUtil.findPathFile(pptPath);
            for (int i = 0; i < xmlList.size(); i++) {
                String xml = "slide" + (i + 1) + ".xml";
                log.info("xml: {}", pptPath + xml);
                PPTSlideRead pptSlideRead = new PPTSlideRead(pptPath + xml);
                allPageElements.add(pptSlideRead.queryElement());
            }
            return allPageElements;
        } finally {
            try {
                assert new File(pptPath).delete();
            } catch (Throwable t) {
                log.error("【文件缓存释放】 释放出错", t);
            }
        }
    }

    @Override
    public List<PPTPageRead> readPPTToReadElements(String filePath) {
        String realUuid = findPptImgPath(filePath, UUID.randomUUID().toString());
        List<List<ReadElement>> pages = readPPT(filePath).stream()
                .map(page -> page.stream()
                        .map(PositionUtil::findReadElementByElement)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        List<PPTPageRead> pageReads = Lists.newArrayList();
        for (int i = 0; i < pages.size(); i++) {
            pageReads.add(new PPTPageRead(pages.get(i),  "/img/" + realUuid + "/" + (i + 1) + ".PNG"));
        }
        return pageReads;
    }

    @Override
    public void cachePPTImg() {
        DataFilePath dataFilePath = dataFilePathService.findLastNoImgPath();
        String path = getPath(dataFilePath);
        int i = 0;
        while(Strings.isNotEmpty(path)) {
            i++;
            if (i> 100)
                break;
            try {
                if (new File(path).exists()) {
                    findPptImgPath(path, UUID.randomUUID().toString());
                } else {
                    dataFilePath.setImgPath("-1");
                    dataFilePathService.update(dataFilePath);
                }
                dataFilePath = dataFilePathService.findLastNoImgPath();
                path = getPath(dataFilePath);
            } catch (Throwable t) {
                log.error("运行出错", t);
            }
        }
    }

    private String getPath(DataFilePath dataFilePath) {
        return Optional.ofNullable(dataFilePath).map(DataFilePath::getPath).orElse(null);
    }

    private String findPptImgPath(String filePath, String uuid) {
        List<DataFilePath> filePaths = dataFilePathService.findByPath(filePath);
        List<DataFilePath> paths = filePaths.stream()
                .filter(path -> Strings.isNotEmpty(path.getImgPath()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(paths)) {
            buildImgInPath(filePath, uuid);
            filePaths.stream()
                    .peek(path -> path.setImgPath(uuid))
                    .forEach(path -> dataFilePathService.update(path));
            return uuid;
        }
        return paths.get(0).getImgPath();
    }

    @SneakyThrows
    private void buildImgInPath(String filePath, String uid) {
        String pngPath = "D:\\Program Files\\phpstudy_pro\\WWW\\img\\" + uid;
        File file = new File(pngPath);
        try {
            file.mkdirs();
            officeLibrary.syncPPT2Jpg(filePath, pngPath, null);
        } catch (Throwable t) {
            try {
                file.delete();
            } catch (Throwable ignored) {}
        }
    }

    private String unzipPPT(String file) {
        String unzipPath = PathUtil.buildPathToUnZip(file);
        PathUtil.checkIsPPTPath(unzipPath);
        return unzipPath + "ppt" + SEPARATOR + "slides" + SEPARATOR;
    }

}
