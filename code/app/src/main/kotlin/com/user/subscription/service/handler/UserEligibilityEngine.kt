package com.user.subscription.service.handler

import com.user.subscription.service.model.SubscriptionPlan
import com.warnermedia.rulesengine.*


object UserEligibilityEngine {
    fun getEngine(): Engine {
        return Engine(
            "user-eligibility-engine", arrayListOf(
                Rule(
                    SubscriptionPlan.STANDARD_MONTHLY.name,
                    arrayListOf(
                        Condition("loggedIn", Operator(OperatorType.EQUALS, true)),
                        Condition("DISCOUNTED_MONTHLY", Operator(OperatorType.EQUALS, false))
                    ),
                ), Rule(
                    SubscriptionPlan.STANDARD_ANNUAL.name,
                    arrayListOf(
                        Condition("loggedIn", Operator(OperatorType.EQUALS, true))
                    ),
                ), Rule(
                    SubscriptionPlan.STUDENT_MONTHLY.name,
                    arrayListOf(
                        Condition("email", Operator(OperatorType.ENDS_WITH, ".edu"))
                    ),
                ), Rule(
                    SubscriptionPlan.PREMIUM_MONTHLY.name,
                    arrayListOf(
                        Condition(
                            "subscribedForMonths",
                            Operator(OperatorType.GREATER_THAN, UserEligibilityConstants.PREMIUM_PLAN_MINIMUM_MONTHS)
                        ), Condition(
                            "currentTime",
                            Operator(OperatorType.GREATER_THAN, UserEligibilityConstants.PREMIUM_PLAN_ROLLOUT_TIME)
                        )
                    ),
                ), Rule(
                    SubscriptionPlan.DISCOUNTED_MONTHLY.name, arrayListOf(
                        Condition(
                            "promotionCode",
                            Operator(OperatorType.CONTAINED_IN, UserEligibilityConstants.ELIGIBLE_PROMO_CODES)
                        )
                    ), options = RuleOptions(priority = 1)
                )
            ), EngineOptions(
                sortRulesByPriority = true,
                storeRuleEvaluationResults = true,
                undefinedFactEvaluationType = UndefinedFactEvaluation.EVALUATE_TO_FALSE
            )
        )
    }
}
