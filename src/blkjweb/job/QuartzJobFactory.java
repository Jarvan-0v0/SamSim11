package blkjweb.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import blkjweb.utils.Console;
import blkjweb.utils.MysqlBack;

/*
 * 针对Quartz的动态维护的定时任务运行工厂类
 */
@DisallowConcurrentExecution
public class QuartzJobFactory implements Job 
{

	public QuartzJobFactory() {  }

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		
		ScheduleJob scheduleJob = (ScheduleJob)context.getMergedJobDataMap().get("scheduleJob");
		/*
		MyLogger.showMessage("定时任务开始执行，任务名称[" + scheduleJob.getJobName() + "]");
		Date previousFireTime = context.getPreviousFireTime();  
		MyLogger.showMessage(""+previousFireTime);
		if(null != previousFireTime){  
			MyLogger.showMessage("定时任务上次调度时间：" + previousFireTime);  
		}  
		MyLogger.showMessage("定时任务下次调度时间：" + context.getNextFireTime());  
        */   
		// 执行业务逻辑
		String status = scheduleJob.getJobStatus();//
		if(status.equals("1"))//状态：1启用 (0禁用 不应该出现）
		{
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
			String file = sdf.format(date) + ".sql";

			MysqlBack dbBack = new MysqlBack();
			if (dbBack.backup(file))
				Console.showMessage(scheduleJob.getJobName() + "_定时备份成功_"+date);
			else
				Console.showMessage(scheduleJob.getJobName() + "_定时备份失败_"+date);
		}
	}
}