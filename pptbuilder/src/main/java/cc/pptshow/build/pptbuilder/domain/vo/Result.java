package cc.pptshow.build.pptbuilder.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private boolean success;

    private Object data;

    public static Result buildSuccessResult() {
        return new Result(true, "成功！");
    }

    public static Result buildSuccessData(Object data) {
        return new Result(true, data);
    }

}
