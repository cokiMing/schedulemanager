package com.cokiming.common.http;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/7.
 */
public class HttpUtil {

    public final static String METHOD_GET = "GET";

    public final static String METHOD_POST = "POST";

    public final static String METHOD_PUT = "PUT";

    public final static String METHOD_DELETE = "DELETE";

    public static String post(String url) throws Exception {
        return Request.Post(url)
                .bodyString(null, ContentType.APPLICATION_JSON)
                .execute().returnContent().asString();
    }

    public static String put(String url) throws Exception {
        return Request.Put(url)
                .bodyString(null, ContentType.APPLICATION_JSON)
                .execute().returnContent().asString();
    }

    public static String get(String url) throws Exception {
        return Request.Get(url).execute().returnContent().asString();
    }

    public static String delete(String url) throws Exception {
        return Request.Delete(url).execute().returnContent().asString();
    }
}
