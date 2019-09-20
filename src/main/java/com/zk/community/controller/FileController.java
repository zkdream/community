package com.zk.community.controller;

import com.zk.community.dto.FileDTO;
import com.zk.community.provider.QiniuProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;


@Controller
public class FileController {

    @Autowired
    private QiniuProvider qiniuProvider;

    @PostMapping("file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request){

        MultipartHttpServletRequest multipartRequest= (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        String url = qiniuProvider.upload(file);
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl(url);
        fileDTO.setMessage("成功");
        return fileDTO;
    }

}
