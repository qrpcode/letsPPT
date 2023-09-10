package cc.pptshow.build.pptbuilder.util;

import java.util.List;

public class MathUtil {

    public static double variance(List<Double> numbers) {
        int m = numbers.size();
        double sum = 0;
        for (Double number : numbers) {//求和
            sum += number;
        }
        double dAve = sum / m;//求平均值
        double dVar = 0;
        for (Double number : numbers) {//求方差
            dVar += (number - dAve) * (number - dAve);
        }
        return dVar / m;
    }


}
