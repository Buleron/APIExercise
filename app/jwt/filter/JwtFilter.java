/*
 * MIT License
 *
 * Copyright (c) 2017 Franz Granlund
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jwt.filter;

import akka.stream.Materializer;
import jwt.JwtValidator;
import models.collection.User;
import models.exceptions.RequestException;
import mongo.MongoDB;
import oauth2.PlatformAttributes;
import play.Logger;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;
import play.routing.HandlerDef;
import play.routing.Router;
import utils.DatabaseUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static oauth2.AuthenticatedAction.getUser;
import static play.mvc.Results.forbidden;
import static utils.Constants.WRONG_TOKEN;


public class JwtFilter extends Filter {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String ROUTE_MODIFIER_NO_JWT_FILTER_TAG = "paKontroll";
    private static final String ERR_AUTHORIZATION_HEADER = "ERR_AUTHORIZATION_HEADER";
    private JwtValidator jwtValidator;

    @Inject
    MongoDB mongoDB;
    @javax.inject.Inject
    HttpExecutionContext context;
    @com.google.inject.Inject
    MessagesApi messagesApi;

    @Inject
    public JwtFilter(Materializer mat, JwtValidator jwtValidator) {
        super(mat);
        this.jwtValidator = jwtValidator;
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> nextFilter, Http.RequestHeader requestHeader) {
        if (requestHeader.attrs().containsKey(Router.Attrs.HANDLER_DEF)) {
            HandlerDef handler = requestHeader.attrs().get(Router.Attrs.HANDLER_DEF);
            List<String> modifiers = handler.getModifiers();

            if (modifiers.contains(ROUTE_MODIFIER_NO_JWT_FILTER_TAG)) {
                return nextFilter.apply(requestHeader);
            }
        }

        Optional<String> authHeader = requestHeader.getHeaders().get(HEADER_AUTHORIZATION);

        if (!authHeader.filter(ah -> ah.contains(BEARER)).isPresent()) {
            Logger.of("f=JwtFilter, error=authHeaderNotPresent");
            return CompletableFuture.completedFuture(forbidden(ERR_AUTHORIZATION_HEADER));
        }
        String token = authHeader.map(ah -> ah.replace(BEARER, "")).orElse("");

        return CompletableFuture.supplyAsync(() ->
                getUser(token, jwtValidator, mongoDB), context.current())
                .thenCompose((user) -> {
                    Http.RequestHeader authUser = requestHeader.addAttr(PlatformAttributes.AUTHENTICATED_USER, user);
                    Http.RequestHeader reqHeader = authUser.withAttrs(authUser.attrs().put(PlatformAttributes.VERIFIED_JWT, token));
                    return nextFilter.apply(reqHeader);
                }).exceptionally((exception) -> {
                    exception.printStackTrace();
                    return DatabaseUtils.resultFromThrowable(exception, messagesApi);
                });
    }
}