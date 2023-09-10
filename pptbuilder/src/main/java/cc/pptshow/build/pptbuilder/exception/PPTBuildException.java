package cc.pptshow.build.pptbuilder.exception;

import lombok.Data;

@Data
public class PPTBuildException extends RuntimeException{
    public PPTBuildException() {
    }

    public PPTBuildException(String msg) {
        super(msg);
    }

    public PPTBuildException(Throwable cause) {
        super(cause);
    }

    public static RuntimeException noSuchCanNotBuild() {
        return new PPTBuildException("当前出现了未定义的case，无法处理！");
    }
}
