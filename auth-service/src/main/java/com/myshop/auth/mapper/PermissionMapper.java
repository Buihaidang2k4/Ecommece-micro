package com.myshop.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {

    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);

    List<String> findPermissionCodesByRoleId(@Param("roleId") Long roleId);
}
