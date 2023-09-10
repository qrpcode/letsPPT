package cc.pptshow.build.pptbuilder.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NlpItemVo {

    private String uri;

    private String formal;

    private String ne;

    private String item;

    @JsonProperty("loc_details")
    private List<String> locDetails;

    @JsonProperty("basic_words")
    private List<String> basicWords;

    @JsonProperty("byte_offset")
    private int byteOffset;

    @JsonProperty("byte_length")
    private int byteLength;

    private String pos;

}
