package com.befash.ai.mapper;

/**
 * Created by NickChung on 01/03/2018.
 */
import com.befash.ai.domain.NewUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * User映射类
 * Created by Administrator on 2017/11/24.
 */
@Mapper
public interface MybatisMapper {

    @Select("SELECT * FROM newuser WHERE uid = #{uid}")
    NewUser findNewUserByUID(@Param("uid") int uid);

    @Select("SELECT * FROM newuser limit 0,1")
    NewUser findOne();

    @Select("select uid from newuser limit 0,1")
    String funcTest();

}
