package com.zk.community.controller;

import com.zk.community.dto.CommentCreateDTO;
import com.zk.community.dto.CommentDTO;
import com.zk.community.dto.ResultDTO;
import com.zk.community.enums.CommentTypeEnum;
import com.zk.community.exception.CustomizeErrorCode;
import com.zk.community.model.Comment;
import com.zk.community.model.User;
import com.zk.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {



    @Autowired
    private CommentService commentService;



    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public ResultDTO post(@RequestBody CommentCreateDTO commentDTO,
                          HttpServletRequest request){

        User user= (User) request.getSession().getAttribute("user");
        if (user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN_IN);
        }
        if (commentDTO==null|| StringUtils.isBlank(commentDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setType(commentDTO.getType());
        comment.setGmtCreat(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreat());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment,user);
        return ResultDTO.okOf();

    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id){
        List<CommentDTO> commentDTOS = commentService.ListByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }

}
