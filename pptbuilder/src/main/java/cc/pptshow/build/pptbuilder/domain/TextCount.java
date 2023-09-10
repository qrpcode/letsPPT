package cc.pptshow.build.pptbuilder.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextCount {

    private int chineseCount;

    private int englishCount;

    private int numberCount;

    private int otherCount;

    public int getExceptChineseCount() {
        return englishCount + numberCount + otherCount;
    }

}
