package com.zk.community.controller;

import com.zk.community.cache.TagCache;
import com.zk.community.dto.QuestionDTO;
import com.zk.community.mapper.QuestionMapper;

import com.zk.community.model.Question;
import com.zk.community.model.User;
import com.zk.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

//    @Autowired
//    private QuestionMapper questionMapper;


    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") long id,
                       Model model){
        QuestionDTO question=questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(value = "id",required = false) Long id,
            HttpServletRequest request,
            Model model
    ){
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());
        if (title==null||title.equals("")){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (description==null||description.equals("")){
            model.addAttribute("error","问题描述不能为空");
            return "publish";
        }
        if (tag==null||tag.equals("")){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
        String invalid = TagCache.filterValid(tag);
        if (!StringUtils.isBlank(invalid)){
            model.addAttribute("error","输入非法标签"+invalid);
            return "publish";
        }



        User user= (User) request.getSession().getAttribute("user");
        if (user==null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(System.currentTimeMillis());
        question.setId(id);

        questionService.createOrupdate(question);
//        questionMapper.create(question);

        return "redirect:/";
    }
}
