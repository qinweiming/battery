package controllers.interceptor;

import models.Quota;
import models.Error;
import models.ErrorCode;
import play.mvc.Before;
import play.mvc.Controller;

/**
 *  API Quota limit
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/4/18 9:42
 */
public class QuotaChecker extends Controller{
    /**
     * check available quota
     */
    @Before
     static void check() {
        String clientId = request.params.get("clientId");
        Quota quota =getQuota(clientId);
        double used=getUsed(clientId);
        boolean hasAvail = Quota.hasAvail(used);
        if (!hasAvail){
            Error error = new Error();
            error.setCodeWithDefaultMsg(ErrorCode.CLIENT_OVER_QUOTA);
            renderJSON(error.toJson());
        }
    }

    private static double getUsed(String clientId) {
        //todo:
        return 0;
    }

    private static Quota getQuota(String clientId) {
        //todo:
        Quota quota=Quota.getCollection(Quota.class).findOne().as(Quota.class);
           return quota;
    }
}
