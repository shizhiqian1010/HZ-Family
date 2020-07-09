package com.hz.family.service.impl;

import com.hz.family.service.BlogService;
import com.hz.family.entity.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    RedisTemplate redisTemplate;

    private final static String BLOG_KEY = "Blog-key";

    private void setBlog(List<Blog> blogList){
        redisTemplate.opsForValue().set(BLOG_KEY,blogList);
    }

    @Override
    public List<Blog> getAllBlog() {
        Object jsonObject = redisTemplate.opsForValue().get(BLOG_KEY);
        List<Blog> blogs = null;
        if (jsonObject != null){
            blogs = (List<Blog>)jsonObject;
        }
        return blogs;
    }

    @Override
    public Boolean addBlog(Blog blog) {
        List<Blog> allBlog = this.getAllBlog();
        if (allBlog == null){
            allBlog = new LinkedList<Blog>();
        }
        allBlog.add(blog);
        this.setBlog(allBlog);
        return true;
    }

    @Override
    public Boolean update(Blog blog) {
        List<Blog> allBlog = this.getAllBlog();
        if (allBlog != null){
            Iterator<Blog> iterator = allBlog.iterator();
            while (iterator.hasNext()){
                Blog next = iterator.next();
                if (next.getId() == blog.getId()){
                    next = blog;
                    break;
                }
            }
            this.setBlog(allBlog);
            return true;
        }
        return false;
    }
}
