---
layout: two-cols
---

#

<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />

[bit.ly/wbd-ato22](https://bit.ly/wbd-ato22)

::right::

<img src="/images/bitly.png" width="500" style="padding: 5rem 2rem 0 5rem;"/>

---
layout: cover
background: "/images/fotografiska.jpeg"
---

# The Power of a Rules Engine

Satvik Shukla

All Things Open '22

---
layout: two-cols
---

# Satvik Shukla

(he/him)

- Software Engineer at Warner Bros. Discovery
- Working on anything/everything commerce
- Creator and maintainer of
  - [WireMock-Captain](https://github.com/HBOCodeLabs/wiremock-captain)
  - [@WarnerMedia/Rules-Engine](https://github.com/WarnerMedia/Rules-Engine)
- Likes
  - watches
  - cooking
  - discovering international music and series
- GitHub: [satvik-s](https://github.com/satvik-s)

::right::

#

<img src="/images/me.jpeg" width="400" height="400" style="padding: 5rem 0 0 5rem;"/>

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# Agenda

- What is a Rules Engine? (~5 mins)

- Use cases (~5 mins)

- Glossary (~5 mins)

- Code walkthrough (~15 mins)

- What's unique about our implementation (~5 mins)

- More advanced examples (~5 mins)

- Q&A (~5 mins)

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# What is a Rules Engine?

<br />
<br />
<br />

> _A rules engine is all about providing an alternative computational model._
> <br />
> ...
> <br />
> _simplistically you can think of it as a bunch of if-then statements._

<br />
<br />

<div style="text-align: right">
    <br />
    <a href="https://martinfowler.com/bliki/RulesEngine.html" rel="noopener noreferrer" target="_blank">Should I use a Rules Engine?</a>
    <br />
    <br />
    <i>Martin Fowler</i>
</div>

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# Use cases

```kotlin{1|2|22|7-9|3|10|14|4|15-18|5|19-21|10-15|all}
fun getUserEligiblePlanTypes( // Hypothetical!
    isLoggedIn: Boolean,
    isStudent: Boolean,
    isEligibleForPremiumPlan: Boolean,
    isEligibleForNewYearsPromotion: Boolean
): Array<String> {
    if (isLoggedIn) {
        return arrayOf("monthly_plan", "annual_plan")
    }
    if (isLoggedIn && isStudent) {
        if (isEligibleForNewYearsPromotion) {
            return arrayOf("monthly_plan", "annual_plan", "student_plan", "new_years_promotional_plan")
        }
        return arrayOf("monthly_plan", "annual_plan", "student_plan")
    }
    if (isLoggedIn && isEligibleForPremiumPlan) {
        return arrayOf("monthly_plan", "annual_plan", "premium_plan")
    }
    if (isLoggedIn && isEligibleForNewYearsPromotion) {
        return arrayOf("monthly_plan", "annual_plan", "new_years_promotional_plan")
    }
    return arrayOf()
}
```

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# Glossary

Frequent terms across rules engine implementations

<v-click>

Condition - An operation leading to a boolean output. e.g.

```kotlin
user_logged_in == true
```

</v-click>

<v-click>

Rule - A collection of conditions leading to a success or failure result. e.g.

```kotlin
user_logged_in == true && user_is_student == true
```

</v-click>

<v-click>

Engine - A collection of rules leading to an overall evaluation result. e.g.

```kotlin
user_logged_in == true && user_is_student == true
user_subscribed_for_months > 12
eligible_promotion_codes.contains(user_input_promotion_code)
```

</v-click>

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---
layout: two-cols
---

# Code walkthrough

GitHub (Code + Slides)

<br />
<br />
<br />
<br />
<br />

[bit.ly/wbd-ato22](https://bit.ly/wbd-ato22)

::right::

<img src="/images/bitly.png" width="400" style="padding: 5rem 0 0 5rem;"/>

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# What's unique about @warnermedia/rules-engine?

Fixed hierarchy

<v-click>

What it supports:

```mermaid
flowchart TD
    E[User Eligibility Engine] --> R1[Standard Monthly Plan Rule]
    E --> R2[Standard Annual Plan Rule]
    R1 --> C1[user_is_logged_in]
    R1 --> C2[user_has_no_existing_plan]
    R2 --> C3[user_is_logged_in]
    R2 --> C4[user_has_no_existing_plan]
    R2 --> C5[year_is_2022]
```

</v-click>

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# What's unique about @warnermedia/rules-engine?

Fixed hierarchy

What it does not support:

```mermaid
flowchart TD
    E[User Eligibility Engine] --> R1[Standard Monthly Plan Rule]
    E --> R2[Standard Annual Plan Rule]
    R2 --> R4[User Has No Plan in 2022 Rule]
    R1 --> C1[user_is_logged_in]
    R1 --> C2[user_has_no_existing_plan]
    R2 --> C3[user_is_logged_in]
    R4 --> C4[user_has_no_existing_plan]
    R4 --> C5[year_is_2022]
    style C1 fill:#CBE6AE
    style C2 fill:#CBE6AE
    style C3 fill:#CBE6AE
    style R4 fill:#FFB3A3
    style C4 fill:#FFB3A3
    style C5 fill:#FFB3A3
```

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# What's unique about @warnermedia/rules-engine?

JSON persistence

<v-click>

An engine instance in code:

```kotlin{3-12|all}
val engine = Engine(
    "user-eligibility-engine",
    arrayListOf(
        Rule(
            SubscriptionPlan.STANDARD_MONTHLY.name,
            arrayListOf(
                Condition("loggedIn", Operator(OperatorType.EQUALS, true)),
                Condition("DISCOUNTED_MONTHLY", Operator(OperatorType.EQUALS, false))
            ),
        ),
        ...,
    ),
    EngineOptions(
        sortRulesByPriority = true,
        storeRuleEvaluationResults = true
    )
)
```

</v-click>

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# What's unique about @warnermedia/rules-engine?

JSON persistence

The engine instance as JSON:

```json{3-13|all}
{
  "id": "user-eligibility-engine",
  "rules": [
    {
      "id": "STANDARD_MONTHLY",
      "conditions": [
        { "fact": "loggedIn", "operator": { "operatorType": "EQUALS", "operatorValue": true } },
        { "fact": "DISCOUNTED_MONTHLY", "operator": { "operatorType": "EQUALS", "operatorValue": false } }
      ],
      "result": { "first": true, "second": false },
      "options": { "conditionJoiner": "AND", "enabled": true, ... }
    }
  ],
  "options": { "sortRulesByPriority": true, "storeRuleEvaluationResults": true, ... }
}
```

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# What's unique about @warnermedia/rules-engine?

Fixed hierarchy + JSON persistence

Visualizing the JSON representation:

<img src="/images/rules-engine-flat.png" width="650"/>

<div style="text-align: right">
    <br />
    Tool used:
    <a href="https://jsongrid.com/json-grid" rel="noopener noreferrer" target="_blank">jsongrid</a>
</div>

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# More advanced examples

Leveraging remote rules engine instances

<img src="/images/rules-engine-s3-bucket.png" width="650"/>

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# More advanced examples

Leveraging remote rules engine instances

```kotlin{18-21|8-16|all}
class CachedEngineInstance(val bucketName: String, val keyName: String, val timeToLive: Int) {
    private val objectMapper = ObjectMapper()
    private val request = GetObjectRequest { key = keyName; bucket = bucketName }
    private val s3Client = S3Client { region = "us-east-1" }
    private var engineInstance: Engine? = null
    private var latestRetrievalTime: Long = 0

    suspend fun getEngineInstance(): Engine {
        // At startup, and every time cache expires, get the updated rules engine instance
        if (engineIsUndefined || engineCacheHasExpired) {
            engineInstance = getRemoteEngineInstance()
        }

        return engineInstance as Engine
    }

    private suspend fun getRemoteEngineInstance(): Engine {
        val responseObject = s3Client.getObject(request) { getObjectResponse -> getObjectResponse.body.toString() }
        return objectMapper.readValue(responseObject, Engine::class.java)
    }
}
```

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# More advanced examples

Leveraging remote rules engine instances

```kotlin{7-8|all}
suspend fun evaluateFacts(facts: HashMap<String, Any?>): EvaluationResult {
    val cachedEngineInstance = CachedEngineInstance(
        "rules-engine-bucket", // name of the S3 bucket
        "engine.json", // name of the file in S3 bucket
        60 * 60 // refresh and get latest persisted engine every hour
    )
    val engine = cachedEngineInstance.getEngineInstance()
    return engine.evaluate(facts)
}
```

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# More advanced examples

Some other (possible) use cases

- Server driven workflows/UI
- CI/CD pipelines
- Feature flags
- Reverse proxy (Gateway)
- _And a lot more..._

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---

# Q&A

FAQs

- When would a rules engine not be applicable?

  - Not as a starting point (_mostly_)
  - Start with something simple (if-then-else)
  - Be mindful of the added complexity with each change

- Is this specific to Kotlin/TypeScript?

  - Not at all!
  - _Computational model_ that can be replicated in any language

- What about all the other (open-source) implementations of rules engine?
  - All are correct!
  - Explore them and make choices based on the use case

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>

---
layout: center
---

__Thank you!__

<br />
<br />

For any feedback about the session, please use the event app.

<br />
<br />
<br />

For any project related questions:
- Approach me!
- Use GitHub [Issues](https://github.com/WarnerMedia/Rules-Engine/pulls)
and [Discussions](https://github.com/WarnerMedia/Rules-Engine/discussions)

<img src="/images/wbd.png" width="80" style="position: absolute; right: 0.2rem; bottom: 0.2rem;"/>
