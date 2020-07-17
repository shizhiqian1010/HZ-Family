package com.hz.family.service.impl;

import com.hz.family.service.BlogService;
import com.hz.family.entity.Blog;
import com.hz.family.util.BlogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public List<Blog> getAllBlog() {
        Set<Blog> range = redisTemplate.opsForZSet().range(BlogUtil.BLOG_CACHE_PREFIX, 0, -1);
        List<Blog> blogs = null;
        if (!CollectionUtils.isEmpty(range)){
            blogs = new ArrayList<>(range);
        }
        return blogs;
    }


    @Override
    public Boolean addBlog(Blog blog) {
        blog.setScore(getBlogSize() +1);
        Boolean add = addBlog(blog,blog.getScore());
        return add;
    }

    @Override
    public Boolean update(Blog blog) {
        Set<Blog> range = redisTemplate.opsForZSet().range(BlogUtil.BLOG_CACHE_PREFIX, blog.getScore(), blog.getScore());
        if (!CollectionUtils.isEmpty(range)){
            Iterator<Blog> iterator = range.iterator();
            while (iterator.hasNext()){
                Blog next = iterator.next();
                if (!StringUtils.isEmpty(blog.getId()) && next.equals(blog.getId())){
                    redisTemplate.opsForZSet().remove(BlogUtil.BLOG_CACHE_PREFIX,next);
                    Boolean add = addBlog(blog,next.getScore());
                    if (add){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Boolean addBlog(Blog blog,Long score){
        Boolean add = redisTemplate.opsForZSet().add(BlogUtil.BLOG_CACHE_PREFIX, blog, score);
        return add;
    }

    private Long getBlogSize(){
        Long size = redisTemplate.opsForZSet().size(BlogUtil.BLOG_CACHE_PREFIX);
        return size;
    }
}
