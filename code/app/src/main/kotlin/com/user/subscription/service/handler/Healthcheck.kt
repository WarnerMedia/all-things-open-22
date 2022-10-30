package com.user.subscription.service.handler

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

class Healthcheck {
    companion object {
        val v1 = Handler<RoutingContext> { req ->
            req.response().end("hello!")
        }
    }
}
