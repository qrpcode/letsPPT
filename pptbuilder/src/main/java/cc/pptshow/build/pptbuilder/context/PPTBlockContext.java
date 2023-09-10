package cc.pptshow.build.pptbuilder.context;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTShape;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PPTBlockContext {

    private static final ThreadLocal<Table<PPTBlockPut, PPTBlock, PPTElement>> BUILD_TABLE = new ThreadLocal<>();
    private static final ThreadLocal<Table<PPTBlockPut, String, String>> COLOR_TABLE = new ThreadLocal<>();

    public static void init() {
        BUILD_TABLE.set(HashBasedTable.create());
        COLOR_TABLE.set(HashBasedTable.create());
    }

    public static Table<PPTBlockPut, PPTBlock, PPTElement> getBuildContext() {
        return BUILD_TABLE.get();
    }

    public static Table<PPTBlockPut, String, String> getColorContext() {
        return COLOR_TABLE.get();
    }

    public static void setBuildContext(Table<PPTBlockPut, PPTBlock, PPTElement> table) {
        BUILD_TABLE.set(table);
    }

    public static void setColorContext(Table<PPTBlockPut, String, String> table) {
        COLOR_TABLE.set(table);
    }

    public static void saveBuildLog(PPTBlockPut pptBlockPut, PPTBlock pptBlock, PPTElement elements) {
        BUILD_TABLE.get().put(pptBlockPut, pptBlock, elements);
    }

    public static void saveColorLog(PPTBlockPut pptBlockPut, String oldColor, String realColor) {
        Table<PPTBlockPut, String, String> table = COLOR_TABLE.get();
        String cacheRealColor = table.get(pptBlockPut, oldColor);
        if (StringUtils.isNotBlank(cacheRealColor) && !StringUtils.equals(realColor, cacheRealColor)) {
            throw new RuntimeException("颜色存储出现错误，color:" + oldColor + "已经存储为" + cacheRealColor);
        }
        if (Objects.nonNull(oldColor)) {
            table.put(pptBlockPut, oldColor, realColor);
        } else {
            table.put(pptBlockPut, BConstant.NULL_COLOR, realColor);
        }
    }

    public static Map<String, String> findColorMap(PPTBlockPut pptBlockPut) {
        return COLOR_TABLE.get().row(pptBlockPut);
    }

    public static Map<PPTBlockPut, Map<PPTBlock, PPTElement>> filterHaveElementMap(PPTElement pptElement) {
        Table<PPTBlockPut, PPTBlock, PPTElement> table = BUILD_TABLE.get();
        return table.rowKeySet().stream()
                .filter(row -> table.row(row).keySet().stream()
                        .anyMatch(block -> table.row(row).containsValue(pptElement)))
                .collect(Collectors.toMap(row -> row, table::row));
    }

    public static PPTBlockPut findUsePPTBlockPut(PPTElement pptElement) {
        Map<PPTBlockPut, Map<PPTBlock, PPTElement>> elementMap = PPTBlockContext.filterHaveElementMap(pptElement);
        return elementMap.keySet().stream().findFirst().orElse(null);
    }

    public static void remove() {
        BUILD_TABLE.remove();
    }
}
