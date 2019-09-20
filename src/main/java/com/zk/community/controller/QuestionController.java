package com.zk.community.controller;

import com.zk.community.dto.CommentDTO;
import com.zk.community.dto.QuestionDTO;
import com.zk.community.enums.CommentTypeEnum;
import com.zk.community.service.CommentService;
import com.zk.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    /**
     *
     */
    @Autowired
    private QuestionService questionService;


    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name="id") long id,Model model){
        QuestionDTO questionDTO= questionService.getById(id);

        List<QuestionDTO> relationQuestions=questionService.selectRelated(questionDTO);
        List<CommentDTO> comments= commentService.ListByTargetId(id, CommentTypeEnum.QUESTION);


        //  累加阅读数
        questionService.incView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        model.addAttribute("relationQuestions",relationQuestions);
        return "question";
    }



}
