package org.blkj.sql.test;

/*在Map 中插入、删除和定位元素，HashMap 是最好的选择。如果要按自然顺序或自定义顺序遍历键，那么TreeMap会更好。
  如果需要输出的顺序和输入的相同,那么用LinkedHashMap
*/

/*
	long startTime,endTime;
	startTime =System.nanoTime();   //获取开始时间
	endTime=System.nanoTime(); //获取结束时间  
	MyLogger.showMessage("程序运行时间： "+(endTime-startTime)+"ns");
 */	

/*
		User user = new User();
		DMLProcessor<User> dbUtil = new DMLProcessor<User>();
		User userT = new User();
		String sql="SELECT * FROM user WHERE id=?";
		user = (User)dbUtil.doQueryOne(userT,sql,new String[]{userID});
		dbUtil.commit();
 */

/*
	DMLProcessor<UserRole> dbUtil = new DMLProcessor<UserRole>();
	List<UserRole> roleIDs = new ArrayList<UserRole>();
	roleIDs = dbUtil.doQueryObjects(UserRole.class,sql,new String[]{userID});
	dbUtil.commit();
	if (Tool.isNull(roleIDs)){
		return null;
	}
	Set<String> set = new HashSet<String>();
	for(int i= 0; i<roleIDs.size(); i++) {
		UserRole role = roleIDs.get(i);
		set.add(role.getRoleid());
	}
*/
	
	
	
	


	
	
	
	