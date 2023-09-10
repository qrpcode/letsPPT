package cc.pptshow.build.pptbuilder.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PPTPageRead {
    private List<ReadElement> elements;
    private String img;

    public PPTPageRead(List<ReadElement> elements, String img) {
        this.elements = elements;
        this.img = img;
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).setNum(i);
        }
    }
}
