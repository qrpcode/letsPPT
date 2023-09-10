package cc.pptshow.build.pptbuilder.service;

import java.util.Set;

public interface DataIconService {

    String buildIconByColor(String color, Set<Integer> exceptIds);

}
