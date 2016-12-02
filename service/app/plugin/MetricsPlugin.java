package plugin;

/**
 * api metrics
 *
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 * date 2016/4/18 9:44
 */


import com.codahale.metrics.*;
import metrics.MongoDBReporter;
import play.Logger;
import play.Play;
import play.PlayPlugin;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MetricsPlugin extends PlayPlugin {
    static String[] esServers = Play.configuration.getProperty("metrics.reporter.es.server",
            "es-server:9300").split(",");
    static int PERIOD = Integer.parseInt(Play.configuration.getProperty("metrics.reporter.period",
            "5"));  //seconds
    public static String mongoUri = Play.configuration.getProperty("metrics.reporter.mongo.uri",
            "mongodb://127.0.0.1:27017/local");
    public static String reporters = Play.configuration.getProperty("metrics.reporters",
            "jmx");
    private static MetricRegistry metricRegistry;

    public static MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }
    List<Reporter> reporterList = new ArrayList<>();
    @Override
    public void afterApplicationStart()  {
        Logger.info("starting MetricsPlugin...");

        metricRegistry = new MetricRegistry();
        if(reporters.contains("console")) {
            ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build();
            reporter.start(PERIOD, TimeUnit.SECONDS);
            reporterList.add(reporter);
        }
        if(reporters.contains("jmx")) {
            final JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).inDomain("novadata").build();
            jmxReporter.start();
            reporterList.add(jmxReporter);
        }
        if(reporters.contains("mongo")) {
            MongoDBReporter mongoDBReporter = MongoDBReporter.forRegistry(metricRegistry)
                    .clientURI(mongoUri)
//                .prefixedWith("api")
                    .build();
            mongoDBReporter.start(PERIOD, TimeUnit.SECONDS);
            reporterList.add(mongoDBReporter);
        }
        //todo:   ElasticsearchReporter test not sucdess with ES 2.2 and 2.3.
//        if(reporters.contains("jmx")) {
//
//            try {
//                ElasticsearchReporter reporter = ElasticsearchReporter.forRegistry(metricRegistry)
//                        .hosts(esServers)
//                        .build();
//                reporter.start(PERIOD, TimeUnit.SECONDS);
//                reporterList.add(reporter);
//            } catch (IOException e) {
//                Logger.error(e, "ElasticsearchReporter error");
//            }
//        }
        Logger.info("MetricsPlugin started.");
    }

    @Override
    public void onApplicationStop() {

        for (Reporter reporter:reporterList)  {
           if(reporter instanceof Closeable){
               try {
                   ((Closeable) reporter).close();
               } catch (IOException e) {
                   Logger.error(e,"metric reporter close exception");
               }
           }
        }
        Logger.info(" MetricsPlugin stopped.");
    }
}

