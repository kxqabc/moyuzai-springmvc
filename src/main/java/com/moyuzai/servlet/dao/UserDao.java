package com.moyuzai.servlet.dao;

import com.moyuzai.servlet.entity.User;
import javafx.scene.chart.ValueAxis;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Created by kong on 17-6-20.
 */
public interface UserDao {

    /**查找*/
    User queryById(long id);

    User queryByMobile(String mobile);

    User queryPasswardByMobile(@Param(value = "mobile") String mobile);

    User queryByMobilePassword(@Param(value = "mobile") String mobile,
                               @Param(value = "password") String password);

    List<User> queryAll(@Param(value = "offset")int offset,
                        @Param(value = "limit")int limit);

    /**修改*/
    int updateByMobile(@Param(value = "mobile") String mobile,
                       @Param(value = "password") String password);

    /**插入*/
    int insertUser(@Param(value = "userName") String userName,
                   @Param(value = "mobile") String mobile,
                   @Param(value = "password") String password);

    int deleteUserById(@Param(value = "userId")long userId);


}
