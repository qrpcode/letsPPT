package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.ppt.util.PPTUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.*;

public class SvgUtil {

    public static void main(String[] args) {
//        String pptPath = "C:\\Users\\qrp19\\Desktop\\1670391073106.pptx";
//        String pptOnlyPath = pptPath.substring(0, pptPath.length() - 5);
//        System.out.println(pptOnlyPath);
//        String mp4Path = pptOnlyPath + "1.mp4";
//        String smallMp4Path = pptOnlyPath + ".mp4";
//        PPTUtil.PPT2MP4(pptPath, mp4Path);
       // PPTUtil.mp4Compression("C:\\Users\\qrp19\\Desktop\\1670392756243.mp4",
       //         "C:\\Users\\qrp19\\Desktop\\s70392756243s.mp4");
        //FileUtil.file(mp4Path).delete();
        //System.out.println(smallMp4Path);
        ImgUtil.convert(FileUtil.file("C:\\Users\\qrp19\\Desktop\\1670392756243.png"),
                FileUtil.file("C:\\Users\\qrp19\\Desktop\\ppt1.jpg"));
    }

    public static String svg2Png(String svgPath) {
        String newPath = svgPath + ".png";
        convertSvg2Png(FileUtil.file(svgPath), FileUtil.file(newPath));
        return newPath;
    }

    @SneakyThrows
    public static void convertSvg2Png(File svg, File png) {
        InputStream in = new FileInputStream(svg);
        OutputStream out = new FileOutputStream(png);
        out = new BufferedOutputStream(out);
        Transcoder transcoder = new PNGTranscoder();
        try {
            TranscoderInput input = new TranscoderInput(in);
            try {
                TranscoderOutput output = new TranscoderOutput(out);
                transcoder.transcode(input, output);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }


}
