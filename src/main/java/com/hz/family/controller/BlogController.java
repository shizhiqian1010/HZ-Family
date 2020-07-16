package com.hz.family.controller;

import com.alibaba.fastjson.JSON;
import com.hz.family.entity.Blog;
import com.hz.family.service.BlogService;
import com.hz.family.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/")
public class BlogController {

    @Autowired
    BlogService blogService;


    @RequestMapping(value = "getAllBlog", method = RequestMethod.GET)
    @ResponseBody
    public String getAllBlog() {
        List<Blog> allBlog = blogService.getAllBlog();
        return JSON.toJSONString(allBlog);
    }

    @RequestMapping(value = "addBlog", method = RequestMethod.POST)
    @ResponseBody
    public String addBlog(@Param("blog") Blog blog) {
        blog.setId(UserUtil.UUID());
        blog.setReleaseTime(new Date());
        blog.setUserName(UserUtil.getCurrentUserName());
        blogService.addBlog(blog);
        return "success";
    }


    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public String update(@Param("blog")Blog blog) {
        blogService.update(blog);
        return "success";
    }


}
