package blkjweb.mybatis;

//import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blkjweb.mybatis.mapper.UserMapper;
import blkjweb.mybatis.model.UserT;
import blkjweb.mybatis.service.UserService;


@Service
public class UserServiceImp implements UserService
{
	//@Resource  都可以运行
	@Autowired
	private UserMapper userMapper;//注入dao
	
	@Override
	public UserT findUserById(String id) 
	{  
		return userMapper.selectUserById(id);  
	}

	
	
}
