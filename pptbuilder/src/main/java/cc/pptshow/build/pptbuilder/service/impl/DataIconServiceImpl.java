package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.DataIcon;
import cc.pptshow.build.pptbuilder.dao.DataIconMapper;
import cc.pptshow.build.pptbuilder.service.DataIconService;
import cc.pptshow.build.pptbuilder.util.PathUtil;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.build.pptbuilder.util.SvgUtil;
import cn.hutool.core.io.file.FileWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataIconServiceImpl implements DataIconService {

    private static final String DEFAULT_COLOR = "1296db";

    @Resource
    private DataIconMapper dataIconMapper;

    @Override
    public String buildIconByColor(String color, Set<Integer> exceptIds) {
        List<DataIcon> dataIcons = dataIconMapper.selectAll();
        List<DataIcon> icons = dataIcons.stream()
                .filter(d -> !exceptIds.contains(d.getId()))
                .collect(Collectors.toList());
        DataIcon icon = RandUtil.round(icons);
        String svgStr = icon.getIconSvg().replace(DEFAULT_COLOR, color);
        String path = PathUtil.randPath("svg");
        log.info("[buildIconByColor] buildIconByColor: {}", path);
        FileWriter writer = new FileWriter(path);
        writer.write(svgStr);
        return SvgUtil.svg2Png(path);
    }
}
