package cc.pptshow.build.pptbuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages = "cc.pptshow.build.pptbuilder.dao")
public class PptbuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PptbuilderApplication.class, args);
    }

}
