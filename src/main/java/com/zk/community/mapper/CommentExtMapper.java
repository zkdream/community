package com.zk.community.mapper;

import com.zk.community.model.Comment;
import com.zk.community.model.CommentExample;
import com.zk.community.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {

    int incComment(Comment record);
}