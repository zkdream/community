package com.zk.community.service;

import com.zk.community.dto.QuestionDTO;
import com.zk.community.mapper.QuestionMapper;
import com.zk.community.mapper.UserMapper;
import com.zk.community.model.Question;
import com.zk.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;


    public List<QuestionDTO> listAll() {
        List<Question> questions=questionMapper.listAll();
        List<QuestionDTO> questionDtos=new ArrayList<QuestionDTO>();
        for (Question question : questions) {
            User user=userMapper.findById(question.getCreator());
            QuestionDTO dto = new QuestionDTO();
            BeanUtils.copyProperties(question,dto);
            dto.setUser(user);
            questionDtos.add(dto);
        }
        return questionDtos;
    }
}
