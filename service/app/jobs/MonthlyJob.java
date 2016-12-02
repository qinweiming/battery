/**
 * 
 */
package jobs;

import play.jobs.Job;
import play.jobs.On;

/**
 * 
 * @author zxy
 * @since Sep 24, 2013 10:45:23 AM
 */
@On("0 59 23 L * ?")
// 每月最后一天的23:59执行
public class MonthlyJob extends Job {
	@Override
	public void doJob() {


	}
}
