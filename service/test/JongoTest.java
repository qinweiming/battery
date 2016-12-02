import com.alibaba.fastjson.JSON;
import com.mongodb.*;
import models.Lead;
import org.apache.commons.lang3.time.StopWatch;
import org.jongo.Jongo;
import org.jongo.MongoCursor;
import org.junit.*;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/7/7 17:00
 */
public class JongoTest {

    public static String mongoUri =          "mongodb://mongodb:NovaData2016@192.168.100.4:27017,192.168.100.5:27017/out?authMechanism=SCRAM-SHA-1&authSource=admin&replicaSet=foba&readPreference=primaryPreferred";
//public static String mongoUri =          "mongodb://192.168.100.6:27017/out";
    static DB db;
    static Jongo jongo;
    @Rule
    public final Stopwatch stopwatch = new Stopwatch() {
        protected void succeeded(long nanos, Description description) {
            System.out.println(description.getMethodName() + " succeeded, time taken ms：" + nanos/1000000);
        }

        /**
         * Invoked when a test fails
         */
        protected void failed(long nanos, Throwable e, Description description) {
            System.out.println(description.getMethodName() + " failed, time taken ms：" + nanos/1000000);
        }

        /**
         * Invoked when a test is skipped due to a failed assumption.
         */
        protected void skipped(long nanos, AssumptionViolatedException e,
                               Description description) {
            System.out.println(description.getMethodName() + " skipped, time taken  ms：" + nanos/1000000);
        }

        /**
         * Invoked when a test method finishes (whether passing or failing)
         */
        protected void finished(long nanos, Description description) {
            System.out.println(description.getMethodName() + " finished, time taken ms：" + nanos/1000000);
        }

    };
    @BeforeClass
    public static void setUp() throws Exception {
        Logger.info("Connecting MongoDB %s",mongoUri);
        MongoClientURI mongoClientURI = new MongoClientURI(mongoUri);
        db = new MongoClient(mongoClientURI).getDB(mongoClientURI.getDatabase());
        jongo = new Jongo(db);
        Logger.info("JongoPlugin started.");

    }

    @AfterClass
    public  static  void tearDown() throws Exception {
        Logger.info("close mongo connection");
        db.getMongo().close();
    }

    @Test
    @Ignore
    public void distinct() throws Exception {
        List<String> strings = jongo.getCollection("lead").distinct("category").as(String.class);
        System.out.println(JSON.toJSONString(strings));
        //strings.forEach(System.out::println);

    }

    @Test
    @Ignore
    public void testArrayParams() throws Exception {
        List<Object> queryParams = new ArrayList<>(4);
        //test empty params array
        MongoCursor<Lead> leads = jongo.getCollection("lead").find("{}",queryParams.toArray()).limit(2).as(Lead.class);
        Assert.assertTrue(leads.hasNext());
        queryParams.add("其他");
        queryParams.add("河南省");
        queryParams.add(Pattern.compile("有限公司"));
        leads = jongo.getCollection("lead").find("{category:#,province:#,companyName:#}",queryParams.toArray()).limit(2).as(Lead.class);
        Assert.assertTrue(leads.hasNext());
        leads.forEach(lead -> System.out.println(lead.toPrettyJson()));

    }
    @Test(expected = IllegalArgumentException.class)
    @Ignore
    public void testMixParams() throws Exception {
        List<Object> queryParams = new ArrayList<>(4);
        queryParams.add("其他");
        queryParams.add("河南省");
        queryParams.add(Pattern.compile("有限公司"));
        MongoCursor<Lead> leads = jongo.getCollection("lead").find("{category:#,province:#,companyName:#,city:#",queryParams.toArray(),"新乡市").limit(2).as(Lead.class);
        leads.forEach(lead -> System.out.println(lead.toPrettyJson()));

    }

    @Test //(timeout = 5000)
    @Ignore
    public void testGeoNear() throws Exception {
        List<Object> queryParams = new ArrayList<>(6);
//        queryParams.add("其他");
//        queryParams.add("河南省");
        queryParams.add(Pattern.compile("公共服务"));
        queryParams.add(103.8924564599);
        queryParams.add(36.05);
        queryParams.add(10);
        List<Lead> leads = jongo.runCommand("{ geoNear :'lead',query: {companyName:#,phone:{$ne:''}},minDistance:0.1,maxDistance:1000000,  near :{ type: 'Point', coordinates:  [#, #]}, spherical : true," +
                " limit:#}",
                queryParams.toArray()
        ).throwOnError().field("results").map(dbObject -> {
            Double dis = (Double) dbObject.get("dis");
            BasicDBObject obj = (BasicDBObject) dbObject.get("obj");
            Lead lead = JSON.parseObject(obj.toJson(), Lead.class);
            lead.setDis(dis);
            return lead;
        });
        leads.forEach(lead->System.out.println(lead.toPrettyJson()));
         Assert.assertEquals(10,leads.size());

    }
    @Test (timeout = 1000)
    public void testFulltextSearch() throws Exception {
        MongoCursor<Lead> cursor = jongo.getCollection("lead").find("{$text:{$search:#}}", "安装").limit(10).as(Lead.class);

        Assert.assertEquals(10,cursor.count());
    }
    @Test (timeout = 1000)
    public void testRegexSearch() throws Exception {
        MongoCursor<Lead> cursor = jongo.getCollection("lead").find("{companyName:#}", Pattern.compile("安装")).limit(10).as(Lead.class);

        Assert.assertEquals(10,cursor.count());
    }
    @Test(expected = MongoCommandException.class)
    public void testGeoNearFulltextSearch() throws Exception {
        List<Object> queryParams = new ArrayList<>(6);
//        queryParams.add("其他");
//        queryParams.add("河南省");
        queryParams.add("公共服务");
        queryParams.add(103.8924564599);
        queryParams.add(36.05);
        queryParams.add(10);
        List<Lead> leads = jongo.runCommand("{ geoNear :'lead',query: {$text:{$search:#}},minDistance:0.1,maxDistance:1000000,  near :{ type: 'Point', coordinates:  [#, #]}, spherical : true," +
                        " limit:#}",
                queryParams.toArray()
        ).throwOnError().field("results").map(dbObject -> {
            Double dis = (Double) dbObject.get("dis");
            BasicDBObject obj = (BasicDBObject) dbObject.get("obj");
            Lead lead = JSON.parseObject(obj.toJson(), Lead.class);
            lead.setDis(dis);
            return lead;
        });
        leads.forEach(lead->System.out.println(lead.toPrettyJson()));
        Assert.assertEquals(10,leads.size());

    }
    @Test(timeout = 3000)
    @Ignore
    public void tesEmptyParamsQueryPerformance() throws Exception {
        List<Object> queryParams = new ArrayList<>(4);

        //test empty params array
        MongoCursor<Lead> leads = jongo.getCollection("lead").find("{entTel:{$ne:''}}",queryParams.toArray()).limit(20).sort("{companyName:-1}").as(Lead.class);
        Assert.assertTrue(leads.hasNext());


    }
    @Test(timeout = 3000)
    @Ignore

    public void testCategoryProvinceCompanyNamePerformance() throws Exception {
        List<Object> queryParams = new ArrayList<>(4);

        //test empty params array
        MongoCursor<Lead> leads;

        queryParams.add("39-计算机、通信和其他电子设备制造业");
        queryParams.add("广东省");
        queryParams.add(Pattern.compile("电"));
        leads = jongo.getCollection("lead").find("{category:#,province:#,companyName:#,phone:{$ne:''}}",queryParams.toArray()).limit(20).sort("{companyName:-1}").as(Lead.class);
        Assert.assertTrue(leads.hasNext());

    }
    @Test(timeout = 3000)
    @Ignore

    public void testProvinceCompanyNamePerformance() throws Exception {
        List<Object> queryParams = new ArrayList<>(4);

        //test empty params array
        MongoCursor<Lead> leads ;

        queryParams.add("广东省");
        queryParams.add(Pattern.compile("软件"));
        leads = jongo.getCollection("lead").withReadPreference(ReadPreference.secondaryPreferred()).find("{province:#,companyName:#,phone:{$ne:''}}",queryParams.toArray()).limit(20).sort("{companyName:-1}").as(Lead.class);
        Assert.assertTrue(leads.hasNext());

    }
    @Test(timeout = 3000)
    @Ignore

    public void testCompanyNamePerformance() throws Exception {
        List<Object> queryParams = new ArrayList<>(4);

        //test empty params array
        MongoCursor<Lead> leads ;
        queryParams.add(Pattern.compile("科技"));
        leads = jongo.getCollection("lead").find("{companyName:#,phone:{$ne:''}}",queryParams.toArray()).limit(20).sort("{companyName:-1}").as(Lead.class);
        Assert.assertTrue(leads.hasNext());

    }

}
