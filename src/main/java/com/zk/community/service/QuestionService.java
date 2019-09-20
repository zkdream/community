package com.zk.community.service;

import com.zk.community.dto.PaginationDTO;
import com.zk.community.dto.QuestionDTO;
import com.zk.community.dto.QuestionQueryDTO;
import com.zk.community.exception.CustomizeErrorCode;
import com.zk.community.exception.CustomizeException;
import com.zk.community.mapper.QuestionExtMapper;
import com.zk.community.mapper.QuestionMapper;
import com.zk.community.mapper.UserMapper;
import com.zk.community.model.Question;
import com.zk.community.model.QuestionExample;
import com.zk.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private QuestionExtMapper questionExtMapper;


    public PaginationDTO list(String search,Integer page, Integer size) {

        if (StringUtils.isNotBlank(search)){
            String[] split = StringUtils.split(search, " ");
            search=Arrays.stream(split).collect(Collectors.joining("|"));
        }
        PaginationDTO paginationDTO = new PaginationDTO();
//        Integer totalCount =(int)questionMapper.countByExample(new QuestionExample());
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount =questionExtMapper.countBySearch(questionQueryDTO);
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
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
//        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        List<Question> questions = questionExtMapper.selectBySeacher(questionQueryDTO);
        List<QuestionDTO> questionDtos = new ArrayList<QuestionDTO>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO dto = new QuestionDTO();
            BeanUtils.copyProperties(question, dto);
            dto.setUser(user);
            questionDtos.add(dto);
        }
        paginationDTO.setData(questionDtos);

        return paginationDTO;
    }

    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {

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
            paginationDTO.setData(questionDtos);
        }
        return paginationDTO;

    }

    public QuestionDTO getById(long id) {
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
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
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

    public void incView(long id) {
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

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");

        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question=new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions=questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
