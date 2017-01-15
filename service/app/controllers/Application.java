package controllers;

import controllers.api.API;
import models.User;
import play.Play;


public class Application extends API {

    public static void index() {
        if (Play.mode.isDev()) {
            redirectToStatic("/public/api/index.html");
        } else {
            redirectToStatic("/public/index.html");

        }

    }

}