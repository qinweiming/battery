package controllers;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import controllers.interceptor.APIRateLimiter;
import controllers.interceptor.APIResponseWrapper;
import controllers.interceptor.ExceptionCatcher;
import controllers.interceptor.RequestLog;
import models.Error;
import models.ErrorCode;
import play.Play;
import play.mvc.With;
import plugin.DataInitPlugin;

import java.time.Instant;
import java.util.Map;

@With({APIRateLimiter.class, RequestLog.class, ExceptionCatcher.class, APIResponseWrapper.class})
public class Application extends BaseController {
    static String version = "2.3";

    public static void status() {

        Map<String, String> status = Maps.newHashMap();
        status.put("status", "ok");
        status.put("version", version);
        status.put("server_time", Instant.now().toString());
        status.put("running_mode",Play.mode.toString());
        renderJSON(JSON.toJSONString(status, true));
    }

    public static void index() {
        redirectToStatic("/public/index.html");
    }

    public static void trainDefaultModel() {
        if (Play.mode.isDev()) {
            DataInitPlugin.trainDefaultModelIfAbsent();
        }
    }
    public static void train() throws Exception {

        if (Play.mode.isDev() || request.isLoopback) {
            TrainJob trainJob = new TrainJob();
            trainJob.now();
            renderText("Train Job started");
        }
    }
    public static void predict() throws Exception {
        if (Play.mode.isDev() || request.isLoopback) {
            PredictJob predictJob = new PredictJob();
            predictJob.now();
            renderText("Predict Job started");
        }
    }
    public static void notFoundPage() {
        Error error = new Error();
        error.setCodeWithDefaultMsg(ErrorCode.CLIENT_RESOURCE_NOT_FOUND);
        notFoundEx(error);
    }
}
