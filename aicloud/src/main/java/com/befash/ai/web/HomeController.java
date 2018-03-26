package com.befash.ai.web;

import com.befash.ai.domain.NewUser;
import com.befash.ai.mapper.MybatisMapper;
import com.befash.ai.service.HelloServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by NickChung on 01/03/2018.
 */
//@RestController
@Controller
@EnableAutoConfiguration
public class HomeController {

    @Autowired
    private MybatisMapper mybatisDao;
    private HelloServiceImpl helloService;

    @RequestMapping("/")
    public String home(Model model) {
        helloService = new HelloServiceImpl(mybatisDao);
        model.addAttribute("message", "SpringBoot + Thymeleaf rocks_" + helloService.funcTest());
        return "system/index";
    }

    @RequestMapping("/test1")
    public NewUser test1() {
        try {
            helloService = new HelloServiceImpl(mybatisDao);
            return helloService.fineOne();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/test2")
    public String test2() {
        try {
            helloService = new HelloServiceImpl(mybatisDao);
            return helloService.funcTest();
        }catch (Exception e){
            return e.toString();
        }
    }
}