package com.foodorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodorder.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND is_deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据用户ID获取Token
     */
    @Select("SELECT token FROM user WHERE id = #{userId} AND is_deleted = 0")
    String getTokenByUserId(@Param("userId") Long userId);

    /**
     * 更新用户Token
     */
    @Update("UPDATE user SET token = #{token} WHERE id = #{userId}")
    int updateToken(@Param("userId") Long userId, @Param("token") String token);

    /**
     * 更新用户新用户状态
     */
    @Update("UPDATE user SET is_new_user = #{isNewUser} WHERE id = #{userId}")
    int updateNewUserStatus(@Param("userId") Long userId, @Param("isNewUser") Integer isNewUser);

    @Update("DELETE FROM user WHERE id = #{userId}")
    int physicalDeleteById(@Param("userId") Long userId);
}
