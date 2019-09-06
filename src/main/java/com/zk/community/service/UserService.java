package com.zk.community.service;

import com.zk.community.mapper.UserMapper;
import com.zk.community.model.User;
import com.zk.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;



    public void creatOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
//        User dbuser = userMapper.findByAccountId(user.getAccountId());
        if (users.size()==0){
//            插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
//            更新
            User dbuser = users.get(0);
            dbuser.setGmtModified(System.currentTimeMillis());
            dbuser.setAvatarUrl(user.getAvatarUrl());
            dbuser.setName(user.getName());
            dbuser.setToken(user.getToken());

            UserExample userExample1 = new UserExample();
            userExample1.createCriteria()
                    .andIdEqualTo(dbuser.getId());
            userMapper.updateByExampleSelective(dbuser, userExample1);
        }

    }
}
