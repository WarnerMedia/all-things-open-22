package com.user.subscription.service.model

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.reflect.full.memberProperties

data class UserSubscriptionRequest(
    @JsonProperty("email") val email: String,
    @JsonProperty("subscribed_for_months") val subscribedForMonths: Int,
    @JsonProperty("logged_in") val loggedIn: Boolean,
    @JsonProperty("promotion_code") val promotionCode: String?
) {
    fun toHashMap(): HashMap<String, Any?> {
        return UserSubscriptionRequest::class.memberProperties.associateTo(hashMapOf()) {
            Pair(
                it.name, it.get(this)
            )
        }
    }
}
