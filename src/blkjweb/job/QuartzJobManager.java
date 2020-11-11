package blkjweb.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.blkj.utils.StringTool;
import org.quartz.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import blkjweb.job.QuartzJobFactory;
import blkjweb.job.ScheduleJob;
import blkjweb.utils.Console;

//获取scheduler方法二：
/*    
    @Controller 类前面
	@Autowired  
	private SchedulerFactoryBean schedulerFactoryBean;  
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
 */ 
public class QuartzJobManager 
{  
	private String jobGroup = "jobGroup_";

	private Scheduler scheduler;  
	{
		//ApplicationContext ctx = new ClassPathXmlApplicationContext("resources/spring/applicationContext.xml");  
		//scheduler = (Scheduler)ctx.getBean("schedulerFactoryBean");
		//获取scheduler方法一： schedulerFactoryBean 由spring创建注入
		scheduler = (Scheduler)new ClassPathXmlApplicationContext("resources/spring/spring.xml").getBean("schedulerFactoryBean");
		
	}

	//创建并启动任务: 处理系统启动时从数据库中独处的任务
	public void  initJob(List<Map<String, Object>> lists)
	{
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
		if(lists != null)
		{
			Map<String, Object> map =  new HashMap<String, Object>();
			for (int i = 0; i < lists.size(); i++)//所有记录
			{
				map = lists.get(i);//每条记录
				ScheduleJob job = new ScheduleJob();
				job = createJob(map);
				if(job != null)
					jobList.add(job);
			}
		}
		if (jobList.isEmpty())
			return;
	    //保存	
		ScheduleJob.setJobList(jobList);
		//任务创建与更新(未存在的就创建，已存在的则更新)
		for (ScheduleJob job : jobList)
		{
			try {
				//获取触发器标识
				TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
				//获取触发器trigger，即spring配置文件中定义的 bean id="myTrigger"
				CronTrigger	trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

				if (null == trigger) {//不存在任务
					addJob(job, trigger, QuartzJobFactory.class);  
				}
				else {//Trigger已存在，更新相应的定时设置
					updateJob(job, triggerKey,trigger);
				}

			} catch (SchedulerException e) {
				Console.showMessage(QuartzJobManager.class.getSimpleName(), e.getMessage());
			}
		}
	}
	//删除任务: 处理页面上请求
	public void  deleteJob(String jobId)
	{
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
		jobList = ScheduleJob.getJobList();
		if(jobList != null)
		for (int i = 0; i < jobList.size(); i++)//所有记录
		{
			ScheduleJob job = new ScheduleJob();
			job = jobList.get(i);
			if(job.getJobId() != null && jobId.equalsIgnoreCase(job.getJobId()))
			{
				TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
				deleteTriggerKey(triggerKey);
				deleteJob(job);
                job.setJobStatus("2");
				jobList.remove(i);
				break;
			}
		}
	}
	
	//创建并启动任务: 处理页面上产生新的Job
	public void  addJob(Map<String, Object> mapRecord) 
	{
		if(mapRecord == null || mapRecord.isEmpty())
			return ;
		
		ScheduleJob job = createJob(mapRecord);
		if(job != null){
			try {
				ScheduleJob.addJob(job);
				//获取触发器标识
				TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
				//获取触发器trigger，即spring配置文件中定义的 bean id="myTrigger"
				CronTrigger	trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

				if (null == trigger) {//不存在任务
					addJob(job, trigger, QuartzJobFactory.class);  
				}
				else {//Trigger已存在，更新相应的定时设置
					updateJob(job, triggerKey,trigger);
				}
			} catch (SchedulerException e) {
				Console.showMessage(QuartzJobManager.class.getSimpleName(), e.getMessage());
			}
		}
	}

	private ScheduleJob createJob(Map<String, Object> map)
	{
		ScheduleJob job = new ScheduleJob();
		
    	//构造每个任务
		int id = (int)map.get("id");
    	job.setJobId(StringTool.intToString(id));
    	job.setJobName((String)map.get("name"));
        job.setJobStatus((String)map.get("status"));//任务状态 0禁用 1启用 2删除
        job.setDesc((String)map.get("descript"));
        job.setJobGroup(jobGroup+id);
        
        //秒  分 时  日 月 周 
        String cronExpression = "0 0 " + map.get("starttime");//执行时间 :小时 
        String mode = (String)map.get("mode");//执行方式：0每天；1周一；2周二。。。。
        if (mode.equals("0"))//每天23点执行一次：0 0 23 * * ?
        	cronExpression += " * * ?"; 
        else //1-7 or SUN-SAT
        	cronExpression += " ? * " + mode; //"0 0 23 ? * MON-FRI" 周一至周五的上午10:15触发
        
       // MyLogger.showMessage("start:" + cronExpression);
        //cronExpression = "0/5 * * * * ?"; //试验用
        job.setCronExpression(cronExpression);
                
        return job; 
	}
	//向任务调度中添加定时任务  
	private void addJob(ScheduleJob job, CronTrigger trigger, Class<? extends Job> cls) throws SchedulerException
	{  
		JobDetail jobDetail = JobBuilder.newJob(cls)//  cls
							  .withIdentity(job.getJobName(), job.getJobGroup())
							  .build();  
		jobDetail.getJobDataMap().put("scheduleJob", job);  
		//表达式调度构建器
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
		//按新的cronExpression表达式构建一个新的trigger
		trigger = TriggerBuilder.newTrigger()
				.withIdentity(job.getJobName(), job.getJobGroup())  
				.withSchedule(scheduleBuilder)
				.build();  
		scheduler.scheduleJob(jobDetail, trigger);  
	}
	
	//修改任务调度中的定时任务    job 定时任务信息 ; triggerKey 定时调度触发键; trigger 定时调度触发器 
	private void updateJob(ScheduleJob job, TriggerKey triggerKey, CronTrigger trigger) throws SchedulerException
	{  
		if (null == job || null == triggerKey || null == trigger) //任务为空  
			return;  
		if (trigger.getCronExpression().equals(job.getCronExpression())) //任务调度表达式一致，不予进行修改！  
			return;

		//Trigger已存在，那么更新相应的定时设置  
		//表达式调度构建器
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());  
		//按新的cronExpression表达式重新构建trigger
		trigger = trigger.getTriggerBuilder()
				.withIdentity(triggerKey)
				.withSchedule(scheduleBuilder)
				.build(); 
		//按新的trigger重新设置job执行
		scheduler.rescheduleJob(triggerKey, trigger);  
	} 
	
	//删除任务调度定时器 
	private void deleteTriggerKey(TriggerKey triggerKey)
	{  
		if(null == triggerKey)  
			return;  
		try {
			scheduler.pauseTrigger(triggerKey);  //停止触发器
			scheduler.unscheduleJob(triggerKey);//移除触发器
		} catch (Exception e) {  
			Console.showMessage(QuartzJobManager.class.getSimpleName(), e.getMessage());
		}  
	}
 
	//删除任务调度中的定时任务 
	private void deleteJob(ScheduleJob job)
	{  
		if (null == job)   
			return;  
		JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());  
		if(null == jobKey)  
			return;  
		
		try {
			scheduler.pauseJob(jobKey);//暂停任务
			scheduler.deleteJob(jobKey);//删除任务后，所对应的trigger也将被删除 
		} catch (SchedulerException e) {  
			Console.showMessage(QuartzJobManager.class.getSimpleName(), e.getMessage());
		}  
	} 

	//立即运行定时任务 
	public void runJob(ScheduleJob job)
	{  
		if (null == job)  
			return;
		try {  
			JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
			
			
			if(null == jobKey)  
				return;  
			scheduler.triggerJob(jobKey);  
		} catch (Exception e) {  
			Console.showMessage(QuartzJobManager.class.getSimpleName(), e.getMessage());
		}  
	}  

   //暂停任务调度中的定时任务 
	public void pauseJob(ScheduleJob job)
	{
		if (null == job)  
			return;
		try {  
			JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());  
			if(null == jobKey)  
				return;  
			scheduler.pauseJob(jobKey);  
		} catch (Exception e) {  
			Console.showMessage(QuartzJobManager.class.getSimpleName(), e.getMessage());
		}  
	}  

	//恢复任务调度中的定时任务 
	void resumeJob(ScheduleJob job)
	{  
		if (null == job) {  
			return;  
		}
		try {  
			JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());  
			if(null == jobKey){  
				return;  
			}  
			scheduler.resumeJob(jobKey);  
		} catch (Exception e) {  
			Console.showMessage(QuartzJobManager.class.getSimpleName(), e.getMessage());  
		}  
	}  

}  
/**
 一个Job可以有多个Trigger，但多个Job不能对应同一个Trigger。
 
 */

//Cron表达式的格式：秒 分 时 日 月 周 年(可选)。
//字段名                 允许的值                        允许的特殊字符  
//秒                         0-59                               , - * /  
//分                         0-59                               , - * /  
//小时                     0-23                               , - * /  
//日                         1-31                               , - * ? / L W C  
//月                         1-12 or JAN-DEC             , - * /  
//周几                     1-7 or SUN-SAT                , - * ? / L C #  
//年 (可选字段)     empty, 1970-2099              , - * /
//“?”字符：表示不确定的值
//“,”字符：指定数个值
//“-”字符：指定一个值的范围
//“/”字符：指定一个值的增加幅度。n/m表示从n开始，每次增加m
//“L”字符：用在日表示一个月中的最后一天，用在周表示该月最后一个星期X
//“W”字符：指定离给定日期最近的工作日(周一到周五)
//“#”字符：表示该月第几个周X。6#3表示该月第3个周五

//每隔5秒执行一次：*/5 * * * * ?
//每隔1分钟执行一次：0 */1 * * * ?
//每天23点执行一次：0 0 23 * * ?
//每天凌晨1点执行一次：0 0 1 * * ?
//每月1号凌晨1点执行一次：0 0 1 1 * ?
//每月最后一天23点执行一次：0 0 23 L * ?
//每周星期天凌晨1点实行一次：0 0 1 ? * L
//在26分、29分、33分执行一次：0 26,29,33 * * * ?
//每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?
//"0 0 12 * * ?" 每天中午12点触发
//"0 15 10 ? * *" 每天上午10:15触发
//"0 15 10 * * ?" 每天上午10:15触发
//"0 15 10 * * ? *" 每天上午10:15触发
//"0 15 10 * * ? 2005" 2005年的每天上午10:15触发
//"0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发
//"0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发
//"0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
//"0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
//"0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发
//"0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发
//"0 15 10 15 * ?" 每月15日上午10:15触发
//"0 15 10 L * ?" 每月最后一日的上午10:15触发
//"0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
//"0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发
//"0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发
//每天早上6点 0 6 * * *
//每两个小时 0 */2 * * *
//晚上11点到早上8点之间每两个小时，早上八点 0 23-7/2，8 * * *
//每个月的4号和每个礼拜的礼拜一到礼拜三的早上11点   0 11 4 * 1-3
//1月1日早上4点  0 4 1 1 *
/*
Quartz大致可分为三个主要的核心：
1、调度器Scheduler:是一个计划调度器容器，容器里面可以盛放众多的JobDetail和Trigger，当容器启动后，里面的每个JobDetail都会根据Trigger按部就班自动去执行.
2、任务Job：要执行的具体内容。JobDetail:具体的可执行的调度程序，包含了这个任务调度的方案和策略。
3、触发器Trigger：调度参数的配置，什么时候去执行调度
spring中所提供的定时任务组件只能够通过修改xml中trigger的配置才能控制定时任务的时间以及任务的启用或停止，这在带给我们方便的同时也失去了动态配置任务的灵活性
实现的Job类加上注解 @DisallowConcurrentExecution即可实现有状态
*/
