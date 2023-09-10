package cc.pptshow.build.pptbuilder.domain.qo;

import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionFilterQo {

    private List<PPTBlockPut> blockPuts;

    private PPTBlockPut originalBlockPut;

    private GlobalStyle globalStyle;

    private Position position;

    public RegionFilterQo(List<PPTBlockPut> blockPuts, BuilderQo builderQo) {
        this.blockPuts = blockPuts;
        this.originalBlockPut = builderQo.getOriginalBlockPut();
        this.globalStyle = builderQo.getGlobalStyle();
        this.position = builderQo.getPosition();
    }
}
