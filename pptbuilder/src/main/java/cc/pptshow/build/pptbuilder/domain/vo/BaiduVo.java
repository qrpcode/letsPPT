package cc.pptshow.build.pptbuilder.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaiduVo {

    private String from;

    private String to;

    @JsonProperty("trans_result")
    private List<TransResult> transResult;

}
