package cc.pptshow.build.pptbuilder.domain.qo;

import cc.pptshow.build.pptbuilder.bean.ImgInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ImgInfoQo extends ImgInfo {

    private String downUrl;

}
