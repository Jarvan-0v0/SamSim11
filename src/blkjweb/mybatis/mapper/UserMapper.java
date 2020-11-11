package blkjweb.mybatis.mapper;

import org.springframework.stereotype.Repository;

import blkjweb.mybatis.model.UserT;

/** 
 *具体的Mapper映射类 
 */ 
@Repository
public interface UserMapper
{
	
    public UserT selectUserById(String userId);  

}  