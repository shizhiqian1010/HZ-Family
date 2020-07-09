package com.hz.family.service;

import com.hz.family.entity.Blog;

import java.util.List;

public interface BlogService {
    List<Blog> getAllBlog();

    Boolean addBlog(Blog blog);

//    Boolean deleteBlog(Blog blog);

    Boolean update(Blog blog);
}
