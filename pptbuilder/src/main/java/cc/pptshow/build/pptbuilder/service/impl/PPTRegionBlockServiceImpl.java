package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.service.PPTRegionBlockService;
import cc.pptshow.ppt.constant.Constant;
import cc.pptshow.ppt.util.FileUtil;
import cc.pptshow.ppt.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class PPTRegionBlockServiceImpl implements PPTRegionBlockService {

    @Override
    public void addRegionBlock(String path) {
        String copyUri = FileUtil.tmpdir() + Constant.SEPARATOR + FileUtil.uuid()+ Constant.SEPARATOR;
        log.info("[addRegionBlock] 文件临时路径: {}", copyUri);
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new RuntimeException("抱歉，没找到您指定的文件");
            }
            FileUtil.traditionalCopy(path, copyUri + "1.zip");
            ZipUtil.zipUncompress(copyUri + "1.zip", copyUri + "pptx");

        } finally {
            File file = new File(copyUri);
            file.delete();
        }
    }
}
