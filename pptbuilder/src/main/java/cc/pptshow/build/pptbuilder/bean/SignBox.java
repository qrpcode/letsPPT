package cc.pptshow.build.pptbuilder.bean;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 令牌盒子，自动获取令牌数据
 */
@Data
@NameStyle(Style.camelhump)
@Table(name = "sign_box")
public class SignBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    /**
     * 请求的签名
     */
    private String sign;

    /**
     * 签名的有效期，注意，这里一般都比实际时间短一些来避免短时间的服务宕机
     */
    private Date effectiveTime;

}
