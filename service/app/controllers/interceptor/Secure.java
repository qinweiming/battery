package controllers.interceptor;

import controllers.BaseController;
import models.Error;
import models.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import play.mvc.Before;

/**
 * 内部简单验证，附带 auth 为 novadata 即可通过
 *
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @version Revision: 1.0
 * @date 15/4/27 下午5:38
 */
public class Secure extends BaseController {

    @Before
    static void checkAccess()  {
        Error error = new Error();
        error.setCodeWithDefaultMsg(ErrorCode.CLIENT_ACCESS_DENIED);
        String accessKey = request.params.get("auth");
        if (StringUtils.isEmpty(accessKey) || !accessKey.equals("novadata")) {
            paramsError("auth");
        }
        String clientId = request.params.get("clientId");
        if (StringUtils.isNotEmpty(clientId)) {
            if (!ObjectId.isValid(clientId)) {
                paramsError("clientId");
            }
        }
    }

}
