package com.zk.community.dto;

import com.zk.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {

    private Long id;


    private Long parentId;


    private Integer type;


    private Long commentator;


    private Long gmtCreat;


    private Long gmtModified;

    private Long likeCount;


    private String content;

    private Long commentCount;

    private User user;
}
