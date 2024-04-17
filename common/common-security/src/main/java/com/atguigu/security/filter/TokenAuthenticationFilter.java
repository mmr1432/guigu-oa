package com.atguigu.security.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.ResponseUtil;
import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.security.custom.LoginUserInfoHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    RedisTemplate redisTemplate;
//    ObjectMapper objectMapper;
    public TokenAuthenticationFilter(RedisTemplate redisTemplate/*,ObjectMapper objectMapper*/){
        this.redisTemplate = redisTemplate;
//        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if ("/admin/system/index/login".equals(httpServletRequest.getRequestURI())){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(httpServletRequest);
        if(null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            ResponseUtil.out(httpServletResponse, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }
    //判断token是否为空
    //从token中获取用户名 通过用户名从redis中取数据
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        if (!StringUtils.isEmpty(token)){
            String username = JwtHelper.getUsername(token);
            //存入ThreadLocal
            LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
            LoginUserInfoHelper.setUsername(username);
            if (!StringUtils.isEmpty(username)){
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                //从redis中获取数据
                String authString = (String)redisTemplate.opsForValue().get(username);
                //将JSON字符串转换为集合类型
                /*try {
                    authorities = objectMapper.readValue(authString,objectMapper.getTypeFactory().constructParametricType(List.class, SimpleGrantedAuthority.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }*/
                List<Map> mapList = JSON.parseArray(authString, Map.class);
                authorities = new ArrayList<>();
                for (Map map : mapList) {
                    authorities.add(new SimpleGrantedAuthority((String)map.get("authority")));
                }
                return new UsernamePasswordAuthenticationToken(username, null, authorities);
            }else
                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        }
        return null;
    }
}
