package com.befash.ai.service;

import com.befash.ai.mapper.MybatisMapper;
import com.befash.ai.domain.NewUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by NickChung on 01/03/2018.
 */
@Service
@Transactional
public class HelloServiceImpl {
    private MybatisMapper mybatisDao;
    public HelloServiceImpl(MybatisMapper mybatisDao){
        this.mybatisDao = mybatisDao;
    }
    public NewUser getNewUser(int uid){
        return mybatisDao.findNewUserByUID(uid);
    }
    public NewUser fineOne(){
        return mybatisDao.findOne();
    }

    public String funcTest(){
        return mybatisDao.funcTest();
    }
}
