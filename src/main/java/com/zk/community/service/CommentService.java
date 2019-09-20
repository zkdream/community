package com.zk.community.service;

import com.zk.community.dto.CommentDTO;
import com.zk.community.enums.CommentTypeEnum;
import com.zk.community.enums.NotificationStatusEnum;
import com.zk.community.enums.NotificationTypeEnum;
import com.zk.community.exception.CustomizeErrorCode;
import com.zk.community.exception.CustomizeException;
import com.zk.community.mapper.*;
import com.zk.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment,User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAMENT_NOT_FOUND);
        }

        if (comment.getType() == null || CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
//            回复评论
            Comment dbcomment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbcomment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            Question question = questionMapper.selectByPrimaryKey(dbcomment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            Comment createComment = new Comment();
            createComment.setId(comment.getParentId());
            createComment.setCommentCount(1L);
            commentExtMapper.incComment(createComment);
//            创建通知
            createNotify(comment, dbcomment.getCommentator(),commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
//            回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            comment.setCommentCount(0L);
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incComment(question);
            createNotify(comment, question.getCreator(),commentator.getName(), question.getTitle(),NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }

    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum typeEnum, Long outerid) {
        if (receiver==comment.getCommentator()){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(typeEnum.getType());
        notification.setOuterid(outerid);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> ListByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_creat desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if (comments.size()==0){
            return new ArrayList<>();
        }
        // 获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds=new ArrayList<>();
        userIds.addAll(commentators);

        // 获取评论人 并且转换成Map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        // 转换comment为commentDTO
           List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
