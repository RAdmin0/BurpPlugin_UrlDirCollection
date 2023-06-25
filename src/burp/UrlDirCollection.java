package burp;

import java.io.*;
import java.util.HashMap;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
// 自定义类
import util.Util;


// IBurpExtender: burp插件必须实现的接口
// IProxyListener: 通过proxy的流量
// IScannerCheck: burp扫描器主动扫描和被动扫描的流量
// IHttpListener: 工具内所有的http流量都经过该监听器

public class UrlDirCollection implements IBurpExtender, IProxyListener, IExtensionStateListener {

    //定义插件信息，方便统一修改
    public static String NAME = "BurpPlugin_UrlDirCollection";
    public static String VERSION = "1.0";

    // 定义callbacks与helpers（一些帮助方法，如url编码等）
    public IBurpExtenderCallbacks callbacks;
    public IExtensionHelpers helpers;

    // 定义输出
    private PrintWriter stdout;
    private PrintWriter stderr;

    public HashMap<String,HashSet<HashMap<String,Integer>>> scan_Urls = new HashMap<>();

    // ----------------------IBurpExtender 实现方法------------------
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        // IBurpExtenderCallbacks 中包含大量的方法，burp调用插件时将传递该对象
        this.callbacks = callbacks;
        // 获取callbacks的帮助对象
        this.helpers = callbacks.getHelpers();

        // 获取到该对象的输出流
        ///this.stdout = stdout.getStdout();
        //this.stderr = callbacks.getStderr();
        // 设置输入及输出
        this.stdout = new PrintWriter(callbacks.getStdout(),true);
        this.stderr = new PrintWriter(callbacks.getStderr(),true);

        stdout.println("this is callbacks.getStdout().println()...");
        stderr.println("this is callbacks.getStderr().println()...");
        callbacks.printOutput("this is printOutput");
        callbacks.printError("this is printError");

        // 设置插件名
        callbacks.setExtensionName(NAME);

        // 向ProxyListener注册该对象
        callbacks.registerProxyListener(this);

        callbacks.registerExtensionStateListener(this);

    }

    // ----------------------IProxyListener 实现方法------------------
    // IProxyListener 需要实现的方法
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        // messageIsRequest 如果时请求 为 true
        // messageIsRequest 如果是响应 为 false

        // 通过 message 获取 IHttpRequestResponse 类型数据 RequestResponse
        IHttpRequestResponse RequestResponse =  message.getMessageInfo();
        IRequestInfo requestInfo = helpers.analyzeRequest(RequestResponse);

        if(messageIsRequest){
            // 处理请求
//            callbacks.printOutput("IProxyListener");

            // callbacks.printOutput("url：" + requestInfo.getUrl());

//            byte[] req = Util.getRequestRaw(message);

            // 获取请求url
            URL req_url = requestInfo.getUrl();

            // 将url分解成多级目录的url
            HashMap<String,HashSet<HashMap<String,Integer>>> urls = Util.get_parent_urls(req_url);
            for (HashSet parent_path: urls.values()) {
                for (String url_schema: urls.keySet()) {
                    output_urlpath(url_schema, parent_path);
                    if(scan_Urls.get(url_schema)==null){
                        scan_Urls.put(url_schema,parent_path);
                    }else{
                        scan_Urls.get(url_schema).addAll(parent_path);
                    }
                }
            }

        }

    }

    public void output_urlpath(String url_schema, HashSet<HashMap<String,Integer>> path){
        for (HashMap<String,Integer> h : path) {
            for (String p: h.keySet()) {
                callbacks.printOutput("URL_PATH: " + url_schema + p );
            }
        }
    }

    //IExtensionStateListener 实现方法
    @Override
    public void extensionUnloaded() throws IOException {
        // 当插件被卸载时，将收集到的 url及路径写入到文件
        String time = String.valueOf(System.currentTimeMillis());
        File f = new File("URLPATH_log" + time + ".csv");
        FileWriter fout = new FileWriter(f);
        String title = "domain_port,path,deep,full_path\n";
        fout.write(title);
        // HashMap<String,HashSet<HashMap<String,Integer>>>
        for (Map.Entry<String, HashSet<HashMap<String, Integer>>> entry :scan_Urls.entrySet()) {
            String domain_port = entry.getKey();
            for (HashMap<String, Integer> path_deeps : entry.getValue()) {
                for (String path : path_deeps.keySet()) {
                    String deep = String.valueOf(path_deeps.get(path));
                    String full_path = domain_port + path;
                    String line = domain_port + "," + path + "," + deep + "," + full_path + "," + "\n";
                    fout.write(line);
                }
            }
        }

        callbacks.printOutput("File " + f.getName() + " Save on Burp Dir: " + f.getAbsolutePath());
        callbacks.printOutput("File " + f.getName() + " Save success！");
        fout.close();

        this.stdout.println("extensionUnloaded()");
        this.stdout.println("Plugin "+ this.NAME +" uninstall");
    }
}
