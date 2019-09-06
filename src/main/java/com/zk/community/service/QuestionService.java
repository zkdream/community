package com.zk.community.service;

import com.zk.community.dto.PaginationDTO;
import com.zk.community.dto.QuestionDTO;
import com.zk.community.exception.CustomizeErrorCode;
import com.zk.community.exception.CustomizeException;
import com.zk.community.mapper.QuestionExtMapper;
import com.zk.community.mapper.QuestionMapper;
import com.zk.community.mapper.UserMapper;
import com.zk.community.model.Question;
import com.zk.community.model.QuestionExample;
import com.zk.community.model.User;
import org.apache.ibatis.session.RowBounds;
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


    @Autowired
    private QuestionExtMapper questionExtMapper;


    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount =(int)questionMapper.countByExample(new QuestionExample());
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }

        if (page > totalCount&totalCount!=0) {
            page = totalCount;
        }

        paginationDTO.setPagination(totalPage, page);
        Integer offset = size * (page - 1);
//        List<Question> questions=new ArrayList<>();
//        List<Question> questions = questionMapper.list(offset, size);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));
        List<QuestionDTO> questionDtos = new ArrayList<QuestionDTO>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO dto = new QuestionDTO();
            BeanUtils.copyProperties(question, dto);
            dto.setUser(user);
            questionDtos.add(dto);
        }
        paginationDTO.setQuestions(questionDtos);

        return paginationDTO;
    }

    public PaginationDTO listByUserId(Integer userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();

        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount =(int)questionMapper.countByExample(example);
//        Integer totalCount = questionMapper.countByUserId(userId);
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (totalPage > 0) {
            if (page < 1) {
                page = 1;
            } else if (page > totalPage) {
                page = totalCount;
            }
            paginationDTO.setPagination(totalPage, page);
            Integer offset = size * (page - 1);

            QuestionExample example1 = new QuestionExample();
            example1.createCriteria()
                    .andCreatorEqualTo(userId);
//            List<Question> questions=new ArrayList<>();
            List<Question> questions = questionMapper.selectByExampleWithRowbounds(example1, new RowBounds(offset, size));
//            List<Question> questions = questionMapper.listByUserId(userId, offset, size);
            List<QuestionDTO> questionDtos = new ArrayList<QuestionDTO>();
            for (Question question : questions) {
                User user = userMapper.selectByPrimaryKey(question.getCreator());
                QuestionDTO dto = new QuestionDTO();
                BeanUtils.copyProperties(question, dto);
                dto.setUser(user);
                questionDtos.add(dto);
            }
            paginationDTO.setQuestions(questionDtos);
        }
        return paginationDTO;

    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrupdate(Question question) {
        if (question.getId()==null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtModified());
            questionMapper.insert(question);
        }else {
            question.setGmtModified(System.currentTimeMillis());
//            Question updateQuestion = new Question();
//            updateQuestion.setGmtModified(System.currentTimeMillis());
//            updateQuestion.setTitle(updateQuestion.getTitle());
//            updateQuestion.setDescription(question);
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            questionMapper.updateByExampleSelective(question, example);
        }

    }

    public void incView(Integer id) {
//        Question question = questionMapper.selectByPrimaryKey(id);
//        Question updateQuestion = new Question();
//        updateQuestion.setViewCount(question.getViewCount()+1);
//        QuestionExample questionExample = new QuestionExample();
//        questionExample.createCriteria()
//                .andIdEqualTo(id);
//        questionMapper.updateByExampleSelective(updateQuestion,questionExample);
        Question updateQuestion = new Question();
        updateQuestion.setViewCount(1);
        updateQuestion.setId(id);
        questionExtMapper.incView(updateQuestion);
    }
}
