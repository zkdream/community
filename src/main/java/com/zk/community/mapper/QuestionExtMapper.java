package com.zk.community.mapper;

import com.zk.community.dto.QuestionQueryDTO;
import com.zk.community.model.Question;
import com.zk.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question record);

    int incComment(Question record);

    List<Question> selectRelated(Question question);

    int countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySeacher(QuestionQueryDTO questionQueryDTO);

}