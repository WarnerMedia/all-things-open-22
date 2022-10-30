package com.user.subscription.service

import com.user.subscription.service.handler.Healthcheck
import com.user.subscription.service.handler.SubscriptionEligibility
import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

class MainVerticle : AbstractVerticle() {

    override fun start() {
        val router = createRouter()
        vertx.createHttpServer().requestHandler(router).listen(8080)
    }

    private fun createRouter() = Router.router(vertx).apply {
        route().handler(BodyHandler.create())
        get("/healthcheck").handler(Healthcheck.v1)
        post("/v1/subscription-eligibility").handler(SubscriptionEligibility.v1)
        post("/v2/subscription-eligibility").handler(SubscriptionEligibility.v2)
    }
}
