package utils;

import com.mongodb.hadoop.MongoInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Level;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.bson.BSONObject;
import play.Logger;
import play.Play;

/**
 * spark support utils
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/4/26 18:11
 */
public class SparkSupport {
    static  SparkConf sparkConf = new SparkConf()
            .set("spark.scheduler.mode", "FAIR")
            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .set("spark.cleaner.ttl", "86400")
            .setIfMissing("spark.master", "local[1]")
            .setAppName("default");

    public static JavaSparkContext getOrCreateSparkContext() {
        turnOffSparkLogs();
        System.setProperty("hadoop.home.dir",Play.configuration.getProperty("hadoop.home.dir",""));
        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(sparkConf));
        if (jsc.isLocal()) {
            Logger.info("-------------spark in local mode-----------");
        }
        return jsc;
    }
    public static  JavaPairRDD<Object, BSONObject> loadMongoCollection(String  collection) {
        String mongoUri =Play.configuration.getProperty("mongo.input.uri",
                "mongodb://127.0.0.1:27017/local");
        Configuration mongodbConfig = new Configuration();
        mongodbConfig.set("mongo.job.input.format",
                "com.mongodb.hadoop.MongoInputFormat");
        mongodbConfig.set("mongo.input.uri", mongoUri+"."+collection);
        JavaPairRDD<Object, BSONObject> documents = SparkSupport.getOrCreateSparkContext().newAPIHadoopRDD(
                mongodbConfig,            // Configuration
                MongoInputFormat.class,   // InputFormat: read from a live cluster.
                Object.class,             // Key class
                BSONObject.class          // Value class
        );
        return documents;
    }
    private static void turnOffSparkLogs() {
        org.apache.log4j.Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
        org.apache.log4j.Logger.getLogger("akka").setLevel(Level.WARN);
    }
}
