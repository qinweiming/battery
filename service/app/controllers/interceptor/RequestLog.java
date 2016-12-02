package controllers.interceptor;

import com.codahale.metrics.Meter;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import plugin.MetricsPlugin;

/**
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @version Revision: 1.0
 * @date 15/6/17 下午4:47
 */
public class RequestLog extends Controller {
    private static Meter requestsMeter = MetricsPlugin.getMetricRegistry().meter("restful-requests");

    @Before
    static void requestLog() {
        requestsMeter.mark();
        Logger.info("request:[%s] params:[%s]", request.method + ":" + request.url, params.allSimple());
    }

}
