package jobs;

import models.Lead;
import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;
import play.test.UnitTest;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/4/29 12:22
 */
public class PredictJobTest extends UnitTest {

    @Test
    public void doJob() throws Exception {
        PredictJob predictJob = new PredictJob();
          predictJob.doJob();
    }

    @Test
    public   void loadAllLeads() throws Exception {
        PredictJob predictJob = new PredictJob();
        JavaRDD<Lead> leadJavaRDD = predictJob.loadAllLeads();
        leadJavaRDD.take(100).forEach(lead -> System.err.println(lead.toJson()));
    }


}