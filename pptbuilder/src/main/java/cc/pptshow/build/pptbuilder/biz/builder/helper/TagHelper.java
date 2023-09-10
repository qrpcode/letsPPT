package cc.pptshow.build.pptbuilder.biz.builder.helper;

import cc.pptshow.build.pptbuilder.bean.DataTag;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.vo.NlpItemVo;
import cc.pptshow.build.pptbuilder.service.DataTagService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TagHelper {

    @Resource
    private DataTagService dataTagService;

    public String saveTagsToIds(List<NlpItemVo> items) {
        List<Integer> ids = Lists.newArrayList();
        for (NlpItemVo item : items) {
            if (item.getItem().length() <= 1) {
                continue;
            }
            DataTag insert = dataTagService.insert(item.getItem());
            ids.add(insert.getId());
        }
        return BConstant.JOINER.join(ids);
    }

}
