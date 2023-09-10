package cc.pptshow.build.pptbuilder.biz.element.impl;

import cc.pptshow.build.pptbuilder.bean.HomePicDecorate;
import cc.pptshow.build.pptbuilder.bean.HomePicShape;
import cc.pptshow.build.pptbuilder.dao.HomePicDecorateMapper;
import cc.pptshow.build.pptbuilder.dao.HomePicShapeMapper;
import cc.pptshow.build.pptbuilder.domain.ShapeFromXml;
import cc.pptshow.build.pptbuilder.biz.element.HomePicBiz;
import cc.pptshow.build.pptbuilder.util.BeanUtil;
import cc.pptshow.build.pptbuilder.util.ShapeUtil;
import cc.pptshow.ppt.constant.Constant;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class HomePicBizImpl implements HomePicBiz {

    @Resource
    private HomePicShapeMapper homePicShapeMapper;

    @Resource
    private HomePicDecorateMapper homePicDecorateMapper;

    private static final String SIDE = "slide";
    private static final String XML = ".xml";
    private static final String P_SP = "<p:sp>";
    private static final String E_P_SP = "</p:sp>";

    @Override
    public void saveElement(String uri, Integer begin, Integer end) {
        try {
            for (int i = begin; i <= end; i++) {
                String filePath = uri + Constant.SEPARATOR + SIDE + i + XML;
                FileInputStream fin = new FileInputStream(filePath);
                InputStreamReader reader = new InputStreamReader(fin);
                BufferedReader buffReader = new BufferedReader(reader);
                StringBuilder sb = new StringBuilder();
                String strTmp;
                while ((strTmp = buffReader.readLine()) != null) {
                    sb.append(strTmp);
                }
                buffReader.close();
                String[] split = sb.toString().split(P_SP);
                List<HomePicShape> shapes = Lists.newArrayList();
                for (int k = 0; k < split.length; k++) {
                    if (split[k].indexOf(E_P_SP) > 1) {
                        ShapeFromXml fromXml = ShapeUtil.toShapeFromXml(split[k]);
                        HomePicShape homePicShape = BeanUtil.shapeFromXml2HomePicShape(fromXml);
                        homePicShape.setShapeOrder(k);
                        shapes.add(homePicShape);
                    }
                }

                HomePicDecorate homePicDecorate = new HomePicDecorate();
                homePicDecorate.setPicStyle(3);
                homePicDecorate.setPicShape(2);
                homePicDecorate.setAboutCn("-");
                homePicDecorateMapper.insertSelective(homePicDecorate);

                for (HomePicShape shape : shapes) {
                    shape.setHomePicDecorateId(homePicDecorate.getId());
                    homePicShapeMapper.insertSelective(shape);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
