package blkjweb.job;

import java.util.ArrayList;
import java.util.List;

//创建任务。既然要动态修改任务，那任务就得保存在某个地方，所以我们需要个JavaBean来存放任务信息。
public class ScheduleJob
{
	private String jobId; //任务id
	private String jobName;//任务名称
	private String jobStatus;//任务状态 0禁用; 1启用； 2删除
	private String cronExpression;//任务运行时间表达式
	private String desc;//任务描述
	private String jobGroup;//任务分组
	
	//保存所有任务
	private static List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
	
	public static List<ScheduleJob> getJobList() {
		return jobList;
	}
	public static void setJobList(List<ScheduleJob> jobList) {
		ScheduleJob.jobList = jobList;
	}
	public static void addJob(ScheduleJob job) {
		ScheduleJob.jobList.add(job);
	}
	
	
	public String getJobId()
	{
		return jobId;
	}

	public void setJobId(String jobId)
	{
		this.jobId = jobId;
	}

	public String getJobName()
	{
		return jobName;
	}

	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}

	public String getJobGroup()
	{
		return jobGroup;
	}

	public void setJobGroup(String jobGroup)
	{
		this.jobGroup = jobGroup;
	}

	public String getJobStatus()
	{
		return jobStatus;
	}

	public void setJobStatus(String jobStatus)
	{
		this.jobStatus = jobStatus;
	}

	public String getCronExpression()
	{
		return cronExpression;
	}

	public void setCronExpression(String cronExpression)
	{
		this.cronExpression = cronExpression;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}


}
