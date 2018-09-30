package com.neo.web;

import com.neo.entity.ShiroToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class HomeController {
    @RequestMapping({"/","/index"})
    public String index(){
        return"/auth/index";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Map<String, Object> map){
        map.put("msg", "请输入账号密码登录");
        return "/auth/login";
    }

    @RequestMapping("/submit")
    public String submit(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) throws Exception{
        String msg = "";
        try{
            ShiroToken token = new ShiroToken(request.getParameter("username"),request.getParameter("password"));
            token.setRememberMe(false);
            SecurityUtils.getSubject().login(token);
        }catch(UnknownAccountException exception) {
            System.out.println("UnknownAccountException -- > 账号不存在：");
            msg = "UnknownAccountException -- > 账号不存在：";
            map.put("msg", msg);
            return "/auth/login";
        }catch(IncorrectCredentialsException exception){
            System.out.println("IncorrectCredentialsException -- > 密码不正确：");
            msg = "IncorrectCredentialsException -- > 密码不正确：";
            map.put("msg", msg);
            return "/auth/login";
        }catch(Exception exception){
            msg = "else >> "+exception;
            System.out.println("else -- >" + exception);
            map.put("msg", msg);
            return "/auth/login";
        }
//      response.sendRedirect("/index");
        return "redirect:/index";
    }

    @RequestMapping("/403")
    public String unauthorizedRole(){
        System.out.println("------没有权限-------");
        return "/auth/403";
    }

}