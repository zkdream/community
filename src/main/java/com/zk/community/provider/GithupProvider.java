package com.zk.community.provider;

import com.alibaba.fastjson.JSON;
import com.zk.community.dto.AccessTokenDTO;
import com.zk.community.dto.GithupUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithupProvider {

    public String getAccessToken(AccessTokenDTO accessTokenDTO) {

        MediaType mediaType
                = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO) );
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String result=response.body().string();
            String[] split = result.split("&");
            String[] split1 = split[0].split("=");
            String token=split1[1];
            System.out.println(result);
            return token;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }


    public GithupUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            GithupUser githupUser= JSON.parseObject(result,GithupUser.class);
            return githupUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
