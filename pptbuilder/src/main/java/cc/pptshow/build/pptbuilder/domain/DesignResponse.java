package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.ppt.element.PPTElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignResponse {

    private List<PPTElement> pptElements;

    private PPTElement pptElement;
    /**
     * 上下文bean，会下一次继续传递过去
     */
    private List<Object> contexts;

    public static DesignResponse buildByRequest(DesignRequest request) {
        DesignResponse response = new DesignResponse();
        response.pptElement = request.getPptElement();
        response.pptElements = request.getPptElements();
        response.contexts = request.getContexts();
        return response;
    }

    public DesignResponse(List<PPTElement> pptElements) {
        this.pptElements = pptElements;
    }

    public DesignResponse(List<PPTElement> pptElements, DesignRequest request) {
        this.contexts = request.getContexts();
        this.pptElements = pptElements;
    }

    public DesignResponse(PPTElement pptElement) {
        this.pptElement = pptElement;
    }

    public DesignResponse(PPTElement pptElement, DesignRequest request) {
        this.contexts = request.getContexts();
        this.pptElement = pptElement;
    }
}
