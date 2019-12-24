package controllers;

import services.PassArgActionService;
import oauth2.PlatformAttributes;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import static play.mvc.Results.ok;

public class ActionControllers {

    @With(PassArgActionService.class)
    public Result playAction(Http.Request request) {
        return ok(request.attrs().get(PlatformAttributes.DASHBOARDACTION));
    }
}