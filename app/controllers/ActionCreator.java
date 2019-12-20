package controllers;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;

public class ActionCreator implements play.http.ActionCreator {
    @Override
    public Action createAction(Http.Request request, Method actionMethod) {
        return new Action.Simple(){
            @Override
            public CompletionStage<Result> call (Http.Request request1){
                return delegate.call(request1);
            }


        };
    }
}
