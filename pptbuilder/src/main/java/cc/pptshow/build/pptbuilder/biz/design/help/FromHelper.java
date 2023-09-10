package cc.pptshow.build.pptbuilder.biz.design.help;

import cc.pptshow.build.pptbuilder.bean.FontInfo;
import cc.pptshow.build.pptbuilder.bean.ImgInfo;
import cc.pptshow.build.pptbuilder.bean.MusicInfo;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.vo.FromVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FromHelper {

    public void addFrom(GlobalStyle globalStyle, ImgInfo imgInfo) {
        List<FromVo> copyrightFromList = globalStyle.getCopyrightFromList();
        String title = "图片（id:" + imgInfo.getId() + ")";
        if (isInFrom(copyrightFromList, title)) {
            return;
        }
        FromVo fromVo = new FromVo();
        fromVo.setName(title);
        fromVo.setLink(imgInfo.getFromUrl());
        fromVo.setFrom(imgInfo.getPicFrom());
        copyrightFromList.add(fromVo);
    }

    private boolean isInFrom(List<FromVo> copyrightFromList, String title) {
        return copyrightFromList.stream().anyMatch(from -> StringUtils.equals(title, from.getName()));
    }

    public void addFrom(GlobalStyle globalStyle, MusicInfo musicInfo) {
        List<FromVo> copyrightFromList = globalStyle.getCopyrightFromList();
        String title = "音乐（id:" + musicInfo.getId() + ")";
        FromVo fromVo = new FromVo();
        fromVo.setName(title);
        fromVo.setLink(musicInfo.getFromUrl());
        fromVo.setFrom(musicInfo.getMusicFrom());
        copyrightFromList.add(fromVo);
    }

    public void addFrom(GlobalStyle globalStyle, FontInfo fontInfo) {
        List<FromVo> copyrightFromList = globalStyle.getCopyrightFromList();
        String title = fontInfo.getFontName() + "（id:" + fontInfo.getId() + ")";
        if (isInFrom(copyrightFromList, title)) {
            return;
        }
        FromVo fromVo = new FromVo();
        fromVo.setName(title);
        fromVo.setLink(fontInfo.getFromUrl());
        fromVo.setFrom(fontInfo.getFontName());
        copyrightFromList.add(fromVo);
    }

}
