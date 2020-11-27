package com.molicloud.mqr.plugin.core.message;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * 图片消息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/14 8:00 下午
 */
public class Img {

    /**
     * 文件对象
     */
    private File file;

    /**
     * 文件远程地址
     */
    private URL fileUrl;

    /**
     * 文件流
     */
    private InputStream fileInputStream;

    private Img() {}

    /**
     * 构造一个图片文件对象
     *
     * @param file
     */
    public Img(File file) {
        this.file = file;
        if (!this.file.exists()) {
            throw new RuntimeException("文件不存在");
        }
    }

    /**
     * 构建一个远程图片文件对象<br/>
     *
     * 【请注意】如果文件地址是https协议，请在程序运行主机安装ssl证书，否则文件资源无法请求
     *
     * @param fileUrl
     */
    public Img(URL fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 构建一个图片文件流对象
     *
     * @param fileInputStream
     */
    public Img(InputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    /**
     * 构建一个本地图片文件对象
     *
     * @param localFileUri
     */
    public Img(String localFileUri) {
        this.file = new File(localFileUri);
        if (!this.file.exists()) {
            throw new RuntimeException("文件不存在");
        }
    }

    /**
     * 获取文件资源
     *
     * @return
     */
    public Object getFileResource() {
        return this.file != null ? this.file : (this.fileUrl != null ? this.fileUrl : this.fileInputStream);
    }
}