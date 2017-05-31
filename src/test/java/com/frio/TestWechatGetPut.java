package com.frio;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.javalite.http.Get;
import org.javalite.http.Http;
import org.javalite.http.Put;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by frio on 17/5/8.
 */
public class TestWechatGetPut {
//    @Test
    public void testGetImage() throws IOException {
        String url = "http://static.qa.91jkys.com/attachment/2017-05-08/1524501/fab1d939-81b2-469c-a657-ed1bac725bd4.jpg";
        Get get = Http.get(url);
        if (get.responseCode() != 200) {
            throw new RuntimeException("request failed, code:" + get.responseCode());
        }
        Map<String, List<String>> headers = get.headers();
        byte[] data = get.bytes();
        if (headers.get("Content-Type") != null
                && headers.get("Content-Type").size() > 0
                && headers.get("Content-Type").get(0).contains("image")) {
            BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(data));
            System.out.println(bimg.getWidth());
            System.out.println(bimg.getHeight());
        }
    }
}
