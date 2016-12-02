package jobs;

import org.junit.Test;
import play.test.UnitTest;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/4/30 20:20
 */
public class TrainJobTest extends UnitTest {

    @Test
    public void doJob() throws Exception {
            TrainJob trainJob = new TrainJob();
        trainJob.doJob();
    }

    @Test
    public void trainRandomForestModel() throws Exception {

    }

    @Test
    public void loadTrainingData() throws Exception {

    }
}