package com.frio.tools.http;

import java.util.Arrays;

/**
 * Created by frio on 17/6/2.
 */
public class CookieUtil {
    public static Cookie cookieParse(String setCookieValue){
        String[] strs = setCookieValue.split(";");
        Cookie cookie = new Cookie(strs[0].split("=")[0], strs[0].split("=")[1]);
        Arrays.asList(strs).stream().forEach(d -> {
            String[] kv = d.split("=");
            switch (kv[0]){
                case "Domain":
                    cookie.setDomain(kv[1].trim());
                    break;
                case "Max-Age":
                    cookie.setMaxAge(Integer.valueOf(kv[1].trim()));
                    break;
                case "Expires":
                    break;
                case "Path":
                    cookie.setPath(kv[1].trim());
                    break;
                case "HttpOnly":
                    cookie.setHttpOnly();
                    break;
                default:
                    break;
            }
        });
        return cookie;
    }
}
