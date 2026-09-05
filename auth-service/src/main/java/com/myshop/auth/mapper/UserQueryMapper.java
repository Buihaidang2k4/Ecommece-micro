package com.myshop.auth.mapper;

import com.myshop.auth.dto.response.UserListRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserQueryMapper {

    boolean userHasRole(@Param("userId") Long userId, @Param("roleName") String roleName);

    List<String> findRoleNamesByUserId(@Param("userId") Long userId);

    List<UserListRow> findUsers(@Param("email") String email,
                                @Param("enabled") Boolean enabled,
                                @Param("roleName") String roleName);
}
