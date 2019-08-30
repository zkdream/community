package com.zk.community.controller;

import com.zk.community.dto.AccessTokenDTO;
import com.zk.community.dto.GithupUser;
import com.zk.community.provider.GithupProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {

    @Autowired
    private GithupProvider githupProvider;

    @Value("${githup.client.id}")
    private String clientId;

    @Value("${githup.client.secret}")
    private String clientSecret;

    @Value("${githup.client.url}")
    private String clientUrl;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(clientUrl);
        accessTokenDTO.setState(state);
        String accessToken = githupProvider.getAccessToken(accessTokenDTO);
        GithupUser user = githupProvider.getUser(accessToken);
        System.out.println(user.getName());
        return "index";
    }

}
