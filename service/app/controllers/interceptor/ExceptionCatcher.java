package controllers.interceptor;

import com.codahale.metrics.Counter;
import models.Error;
import models.ErrorCode;
import play.Logger;
import play.Play;
import play.mvc.Catch;
import play.mvc.Controller;
import plugin.MetricsPlugin;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/5/12 9:52
 */
public class ExceptionCatcher extends Controller {
    private static Counter errorCounter = MetricsPlugin.getMetricRegistry().counter("restful-error");

    @Catch(value = Throwable.class, priority = 1)
    static void catchThrowable(Throwable throwable) {
        errorCounter.inc();
        Logger.error("EXCEPTION %s", throwable);
        Error error = new Error();
        error.setCode(ErrorCode.SERVER_INTERNAL_ERROR);
        error.setMessage("Server throwed Exception:" + throwable.getMessage());
        if (Play.mode.isDev())
            error.setDetailWithExecption(throwable);
        error(error.toPrettyJson());

    }
}
