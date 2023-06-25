# BurpPlugin_UrlDirCollection
使用burp对所有经过Proxy的请求进行收集Url，对域名，目录，目录层级进行收集，存放在csv文件中，可以使用excel方便的对目标进行过滤，收集一级、二级目录等，然后使用dirsearch进行扫描
如图，可通过筛选进行过滤
![image](https://github.com/Rchilde/BurpPlugin_UrlDirCollection/assets/48618751/6f3af349-81cf-4d72-9c6d-0fb757dd2825)

在插件中勾选使用该插件即可启用被动收集功能
![image](https://github.com/Rchilde/BurpPlugin_UrlDirCollection/assets/48618751/03b2130e-d926-4fb9-968a-b7a4d36a118e)

使用完毕后点击卸载该插件，即可将收集到的url存入到burp的目录中，可根据日志输出找到文件
![image](https://github.com/Rchilde/BurpPlugin_UrlDirCollection/assets/48618751/e5973616-b557-4f7a-abed-856dc23fb74a)
