package com.zk.community.cache;

import com.zk.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {


    public static List<TagDTO> get() {

        List<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO programs = new TagDTO();
        programs.setCategoryName("开发语言");
        programs.setTags(Arrays.asList("javascript", "php", "css", "html", "html5", "java", "node.js", "python", "c++", "c", "golang", "objective-c", "typescript", "shell c#", "swift", "sass", "bash", "ruby", "less", "asp.net", "lua scala", "coffeescript", "actionscript", "rust", "erlang", "perl"));
        tagDTOS.add(programs);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台架构");
        framework.setTags(Arrays.asList("laravel", "spring", "express", "django", "flask", "yii", "ruby-on-rails tornado", "koa", "struts"));
        tagDTOS.add(framework);


        TagDTO services = new TagDTO();
        services.setCategoryName("服务器");
        services.setTags(Arrays.asList("linux", "nginx", "docker", "apache", "ubuntu", "centos", "缓存 tomcat", "负载均衡", "unix", "hadoop", "windows-server"));
        tagDTOS.add(services);

        TagDTO dbs = new TagDTO();
        dbs.setCategoryName("数据库和缓存");
        dbs.setTags(Arrays.asList("mysql", "redis", "mongodb", "sql", "oracle", "nosql memcached", "sqlserver", "postgresql", "sqlite"));
        tagDTOS.add(dbs);


        return tagDTOS;


    }


    public static String filterValid(String tags) {
        String[] split = StringUtils.split(tags, ",");
        List<TagDTO> tagDTOS = get();
        List<String> tagList = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;

    }


}
