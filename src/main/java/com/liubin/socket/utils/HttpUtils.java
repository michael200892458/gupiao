package com.liubin.socket.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;

/**
 * Created by liubin on 2014/12/10.
 */
public class HttpUtils {
    public static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .build();


    public static String getResponse(String scheme, String host, int port, String path, Map<String, String> params) throws Exception {
        URI uri = buildUri(scheme, host, port, path, params);
        return createHttpRequest(CommonConstants.HTTP_METHOD_GET, uri);
    }

    public static String getResponse(String url) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        return createHttpRequest(CommonConstants.HTTP_METHOD_GET, uriBuilder.build());
    }

    private static URI buildUri(String scheme, String host, int port, String path, Map<String, String> params) throws Exception {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(scheme)
                    .setHost(host)
                    .setPath(path);
            if (port != 80) {
                uriBuilder.setPort(80);
            }
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            return uriBuilder.build();
        } catch (Exception e) {
            throw e;
        }
    }


    private static String gzipContentDecompress(GzipDecompressingEntity decompressingEntity) throws Exception {
        InputStream inputStream = decompressingEntity.getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "gbk"));
        try {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            bufferedReader.close();
        }
    }

    private static String createHttpRequest(String method, URI uri) throws Exception {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            if (CommonConstants.HTTP_METHOD_GET.equals(method)) {
                HttpGet httpGet = new HttpGet(uri);
                httpGet.setConfig(requestConfig);
                response = httpClient.execute(httpGet);
            } else if (CommonConstants.HTTP_METHOD_POST.equals(method)) {
                HttpPost httpPost = new HttpPost(uri);
                httpPost.setConfig(requestConfig);
                response = httpClient.execute(httpPost);
            }
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity instanceof GzipDecompressingEntity) {
                return gzipContentDecompress((GzipDecompressingEntity)httpEntity);
            }
            return httpEntity == null ? null : EntityUtils.toString(httpEntity, "gbk");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e){
                throw e;
            }
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public static String postResponse(String scheme, String host, int port, String path, Map<String, String> params) throws Exception {
        URI uri = buildUri(scheme, host, port, path, params);
        return createHttpRequest(CommonConstants.HTTP_METHOD_POST, uri);
    }

    public static String getUrl(String url) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        return createHttpRequest(CommonConstants.HTTP_METHOD_GET, uriBuilder.build());
    }

    public static String postRaw(String url, String data) throws Exception {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(data, "utf-8");
            httpPost.setEntity(stringEntity);
            httpPost.setConfig(requestConfig);
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            return httpEntity == null ? null : EntityUtils.toString(httpEntity, "utf-8");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e){
                throw e;
            }
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
