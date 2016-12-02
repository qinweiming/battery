package jobs;

import org.junit.Test;
import play.Play;
import play.test.UnitTest;
import plugin.DataInitPlugin;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/5/1 13:27
 */
public class DataInitializerTest extends UnitTest {

    public static final String SECRET_KEY = "146bcbd5448a9bc31a88ebd1da4037f11268912c5";
    public static final String ACCESS_KEY_ID = "1oKIHohJJtA/6NxWHesCHOw==";
    private String leadId= "5724440cd53a97ad197c340c";
    public static final String CLIENT_ID = "53ea055e0cf2921b57a24e2c";
    @Test
    public void afterApplicationStart() throws Exception {
        DataInitPlugin dataInitPlugin = new DataInitPlugin();
        dataInitPlugin.afterApplicationStart();
    }

    @Test
    public void initData() throws Exception {
        DataInitPlugin.initData();
    }


    @Test
    public void trainOrLoadDefaultModel() throws Exception {
        DataInitPlugin.trainDefaultModelIfAbsent();

    }
    @Test
    public void loadDefaultSampleData(){
        DataInitPlugin.loadDefaultSampleData().saveAsTextFile(Play.configuration.getProperty("ml.resources", "resources/") + "sample_data_nominal.txt");
    }
}