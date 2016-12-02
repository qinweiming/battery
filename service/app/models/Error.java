package models;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 用于api服务器端返回客户端的响应
 *
 * @author zhaoxy
 */
public final class Error implements Jsonable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回信息代码（参见{@link ErrorCode} ）
     */
    private Integer code = ErrorCode.SUCCESS;

    /**
     * 返回的消息描述
     */
    private String message = "";

    /**
     * 详细消息描述信息（如：后台详细的异常信息）
     */
    private String detail;

    public Error() {
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setCodeWithDefaultMsg(int code) {
        this.code = code;
        this.message = ErrorCode.getMsg(code);
    }

    public void setCodeMsg(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    /**
     * 设置参数缺失的返回
     *
     * @param paramNames
     */
    public Error setParameterMiss(String... paramNames) {
        this.code = ErrorCode.CLIENT_FORMAT_ERROR;
        this.message = "missing parameter [" + StringUtils.join(paramNames, ",") + "]";
        return this;
    }

    /**
     * 设置参数缺失的返回
     *
     * @param paramNames
     */
    public Error parameterValueError(String... paramNames) {
        this.code = ErrorCode.CLIENT_FORMAT_ERROR;
        this.message = "parameter value error [" + StringUtils.join(paramNames, ",") + "]";
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDetailWithExecption(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        this.detail = stringWriter.toString();
    }

    public boolean success() {
        return ErrorCode.SUCCESS == this.code;
    }

    public Error resourceNotFound(String... resourceIds) {

        this.code = ErrorCode.CLIENT_RESOURCE_NOT_FOUND;
        this.message = "resource not found,resource ids [" + StringUtils.join(resourceIds, ",") + "]";
        return this;
    }
}
