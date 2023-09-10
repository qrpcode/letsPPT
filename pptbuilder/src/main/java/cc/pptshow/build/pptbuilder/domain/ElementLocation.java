package cc.pptshow.build.pptbuilder.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElementLocation {
    private double left;
    private double top;
    private double width;
    private double height;

    public double getRight() {
        return left + width;
    }

    public double getBottom() {
        return top + height;
    }
}
