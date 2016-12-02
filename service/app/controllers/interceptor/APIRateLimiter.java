package controllers.interceptor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import controllers.BaseController;
import models.Error;
import models.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.mvc.Before;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 实现 API 的限流
 *
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @version Revision: 1.0
 * @date 15/7/17 下午3:44
 *
 */
public class APIRateLimiter extends BaseController {
    private static Cache<String, RateLimiter> cache = CacheBuilder.newBuilder().initialCapacity(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES).build();
    //qps 60
    private static final double DEFAULT_LIMIT = 60;

    @Before
    static void rateLimiting() {
        Error error = new Error();
        String access_key_id = request.params.get("access_key_id");
        if (StringUtils.isNotEmpty(access_key_id)) {
            try {
                RateLimiter rateLimiter = cache.get(access_key_id, () -> RateLimiter.create(DEFAULT_LIMIT));
                if (!rateLimiter.tryAcquire()) { //未请求到limiter则返回超额提示
                    error.setCodeWithDefaultMsg(ErrorCode.CLIENT_OVER_QUOTA);
                    forbidden(error);
                }
            } catch (ExecutionException e) {
                Logger.error("get rate limiter error", e);
            }

        }
    }
}
