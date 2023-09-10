package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.ContentTitle;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ContentTitleMapper extends Mapper<ContentTitle> {

    String COLUMN = " id AS id, icon_svg AS iconSvg, title AS title, title_long AS titleLong, able_order AS AbleOrder ";

    @Select({"<script>",
            " SELECT " + COLUMN + "FROM content_title WHERE able_order = #{order} AND ",
            " <foreach item=\"id\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" > ",
                "#{id}",
            " </foreach> ",
            " ORDER BY RAND LIMIT 1 ",
            "</script>"})
    List<ContentTitle> queryByOrderAndExclude(Integer order, List<Integer> ids);
}
