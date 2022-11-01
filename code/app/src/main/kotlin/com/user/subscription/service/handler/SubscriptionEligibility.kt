package com.user.subscription.service.handler

import com.user.subscription.service.model.SubscriptionPlan
import com.user.subscription.service.model.UserSubscriptionRequest
import com.warnermedia.rulesengine.*
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.DecodeException
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import mu.KotlinLogging
import java.time.Instant

private val logger = KotlinLogging.logger("SubscriptionEligibility")

class SubscriptionEligibility {
    companion object {
        val v1 = Handler<RoutingContext> { req ->
            logger.info { "incoming request to v1 API" }
            val userRequestBody = parseBody(req)

            if (userRequestBody == null) {
                logger.error { "bad user data" }
                req.response().setStatusCode(400).end("Bad Request")
                return@Handler
            }

            logger.info { "user_data - $userRequestBody" }
            val plans = arrayListOf<SubscriptionPlan>()

            if (userRequestBody.loggedIn) {
                plans.addAll(arrayListOf(SubscriptionPlan.STANDARD_MONTHLY, SubscriptionPlan.STANDARD_ANNUAL))
            }

            if (userRequestBody.email.endsWith(".edu")) {
                plans.add(SubscriptionPlan.STUDENT_MONTHLY)
            }

            if (
                userRequestBody.subscribedForMonths > 12 &&
                Instant.now().epochSecond > UserEligibilityConstants.PREMIUM_PLAN_ROLLOUT_TIME) {
                plans.add(SubscriptionPlan.PREMIUM_MONTHLY)
            }

            if (hashSetOf("promo.code.1").contains(userRequestBody.promotionCode)) {
                plans.add(SubscriptionPlan.DISCOUNTED_MONTHLY)
                plans.remove(SubscriptionPlan.STANDARD_MONTHLY)
            }

            logger.info { "plans - $plans" }
            req.response().endWithJson(plans)
        }

        val v2 = Handler<RoutingContext> { req ->
            logger.info { "incoming request to v2 API" }
            val userRequestBody = parseBody(req)

            if (userRequestBody == null) {
                logger.error { "bad user data" }
                req.response().setStatusCode(400).end("Bad Request")
                return@Handler
            }

            logger.info { "user_data - $userRequestBody" }

            val engine = UserEligibilityEngine.getEngine()
            val evaluationResult = engine.evaluate(userRequestBody.toHashMap())
            val plans = evaluationResult.getPlansList()

            logger.info { "plans - $plans" }
            req.response().endWithJson(plans)
        }

        private fun parseBody(requestContext: RoutingContext): UserSubscriptionRequest? {
            return try {
                requestContext.body().asPojo(UserSubscriptionRequest::class.java, 200)
            } catch (error: DecodeException) {
                logger.error { "error - $error" }
                null
            }
        }

        private fun HttpServerResponse.endWithJson(obj: Any) {
            this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
        }

        private fun EvaluationResult.getPlansList(): List<String> {
            return this.ruleEvaluations.filter { it.isSuccess() }.map { it.ruleId }
        }
    }
}
