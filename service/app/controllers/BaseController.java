package controllers;

import models.Error;
import models.ErrorCode;
import play.mvc.Controller;
import play.mvc.results.BadRequest;
import play.mvc.results.NotFound;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/5/12 9:21
 */

public class BaseController extends Controller {
    /**
     * Send a 400 Bad request
     */
    protected static void paramsError(String... paramNames) {
        request.format="json";
        throw new BadRequest(new Error().parameterValueError(paramNames).toPrettyJson());
    }

    protected static void paramsMiss(String... paramNames) {
        request.format="json";
        throw new BadRequest(new Error().setParameterMiss(paramNames).toPrettyJson());
    }

    protected static void notFoundEx(String... resourceIds) {
        request.format="json";
        throw new NotFound(new Error().resourceNotFound(resourceIds).toPrettyJson());
    }

    protected static void notFoundEx(Error error) {
        request.format="json";
        throw new NotFound(error.toPrettyJson());
    }

    protected static void errorJson() {
        request.format="json";
        Error error1 = new Error();
        error1.setCodeWithDefaultMsg(ErrorCode.SERVER_INTERNAL_ERROR);
        error(error1);

    }

    protected static void errorJson(int errorCode, String reason) {
        request.format="json";
        Error error1 = new Error();
        error1.setCode(errorCode);
        error1.setMessage(reason);
        error(error1);

    }

    protected static void errorJson(String reason) {
        errorJson(ErrorCode.SERVER_INTERNAL_ERROR, reason);

    }

    protected static void error(Error error) {
        request.format="json";
        throw new play.mvc.results.Error(error.toPrettyJson());
    }

    protected static void forbidden(Error error) {
        request.format="json";
        throw new play.mvc.results.Error(error.toPrettyJson());
    }

    protected static void unauthorized(Error error) {
        request.format="json";
        throw new play.mvc.results.Error(error.toPrettyJson());
    }
}
