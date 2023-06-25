package util;

import burp.IHttpRequestResponse;
import burp.IInterceptedProxyMessage;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

public  class Util {


    public static HashMap get_parent_urls(URL url_1){
        // 返回值结构 结构为 {域名 : { 路径: 层级 }}

        String domain_port = url_1.getAuthority();
        // 获取所有父目录拼接为url，放入HashSet去重后返回
        String url_schema = url_1.getProtocol() + "://" + domain_port;
        // 获取路径
        String path = url_1.getPath();

        // 存储拼接的 url， 结构为 {域名 : { 路径: 层级 }}
        HashMap<String,HashSet<HashMap<String,Integer>>> urls = new HashMap<>();
        urls.put(url_schema, new HashSet<HashMap<String,Integer>>());

        HashMap parent_path_deep = get_parent_path(path);
        urls.get(url_schema).add(parent_path_deep);
        path = getPath(parent_path_deep);

        // 循环 获取父路径并存放到 urls
        while(!path.equals("")){
            parent_path_deep = get_parent_path(path);
            urls.get(url_schema).add(parent_path_deep);
            path = getPath(parent_path_deep);
        }

        return urls;
    }

    public static String getPath(HashMap parent_path_deep) {
        // 获取 HashMap 中的 路径
        String path = null;
        for (Object p : parent_path_deep.keySet()) {
            path = (String) p;
        }
        return path;
    }
    public static HashMap get_parent_path(String path){
        // 获取父目录及目录层级并返回 {目录 : 层级}
        int parentPathIndexOf = path.lastIndexOf("/");
        String parentPath = path.substring(0, parentPathIndexOf);

        int deep = parentPath.length() - parentPath.replaceAll("/","").length();

        HashMap<String, Integer> path_deep = new HashMap<>();
        path_deep.put(parentPath, deep);

        return path_deep;

    }

}
