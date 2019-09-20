package com.zk.community.provider;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;

import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


@Service
public class QiniuProvider {

    @Value("${qiniu.client.accessKey}")
    private String accessKey;

    @Value("${qiniu.client.secretKey}")
    private String secretKey;

    @Value("${qiniu.client.bucket}")
    private String bucket;

    public String upload(MultipartFile file){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
//...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
      //默认不指定key的情况下，以文件内容的hash值作为文件名
        String[] split = file.getName().split("\\.");
        String key="";
        if (split.length>1){
            key= "community/"+ UUID.randomUUID()+"."+split[split.length-1];
        }else {
            key= "community/"+ UUID.randomUUID();
        }
        //   byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(file.getBytes(), key, upToken);

            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//            System.out.println(putRet.key);
//            System.out.println(putRet.hash);
            return "http://qiniucloud.zklj.com.cn/"+putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
