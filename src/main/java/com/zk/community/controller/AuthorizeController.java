package com.zk.community.controller;

import com.zk.community.dto.AccessTokenDTO;
import com.zk.community.dto.GithupUser;
import com.zk.community.mapper.UserMapper;
import com.zk.community.model.User;
import com.zk.community.provider.GithupProvider;
import com.zk.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
//                           HttpServletRequest request,
                           HttpServletResponse response){

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(clientUrl);
        accessTokenDTO.setState(state);
        String accessToken = githupProvider.getAccessToken(accessTokenDTO);
        GithupUser githupUser = githupProvider.getUser(accessToken);
        if (githupUser!=null&githupUser.getId()!=null){
            User  user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setAvatarUrl(githupUser.getAvatarUrl());
            user.setName(githupUser.getName());
            user.setAccountId(String.valueOf(githupUser.getId()));
//            user.setGmtCreate(System.currentTimeMillis());
//            user.setGmtModified(user.getGmtCreate());
            userService.creatOrUpdate(user);
//            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
//            request.getSession().setAttribute("user",user);
            return "redirect:/";
        }else {
            return "redirect:/";
        }
//        System.out.println(user.getName());
//        return "index";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){

        request.getSession().removeAttribute("user");
        Cookie cookie=new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

}
