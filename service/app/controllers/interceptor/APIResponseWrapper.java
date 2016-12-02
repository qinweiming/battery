package controllers.interceptor;

import play.mvc.After;
import play.mvc.Controller;

/**
 * 设置一些 api response 通用参数. 比如返回的 header 和 cros
 *
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @version Revision: 1.0
 * @date 15/6/17 下午4:48
 */
public class APIResponseWrapper extends Controller {

    @After
    static void headers() {
        //set default content type
        response.setContentTypeIfNotSet("application/json; charset=utf-8");
        //set cors
        response.accessControl("*");
    }
}
