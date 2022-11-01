package com.user.subscription.service.handler

import com.warnermedia.rulesengine.*

//    Rules to add:
//    - standard_monthly - eligible when logged in
//    - standard_annual - eligible when logged in
//    - student_monthly - eligible when email ends with .edu
//    - premium_monthly - eligible when user on platform for more than
//      x months, and it is past launch time
//    - discounted_monthly - eligible when promo code is eligible
//    - standard_monthly - not eligible if discounted_monthly is eligible

object UserEligibilityEngine {
    fun getEngine(): Engine {
        return Engine(
            "user-eligibility-engine",
            arrayListOf(),
            EngineOptions(
                sortRulesByPriority = true,
                storeRuleEvaluationResults = true,
                undefinedFactEvaluationType = UndefinedFactEvaluation.EVALUATE_TO_FALSE
            )
        )
    }
}
