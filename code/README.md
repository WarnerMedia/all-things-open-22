# User Subscription Service

A kotlin-based microservice that handles users' subscriptions

## Setup

Requires:
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Kotlin 1.7.20](https://kotlinlang.org/docs/command-line.html)
- [Gradle 7.4.2](https://gradle.org/install/)
- [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages)

### GitHub Packages

The Rules Engine implementation is published to GitHub Packages and not maven central.
Therefore, the additional step is required to allow the project locally be able to 
download the package published to GitHub.

Once the PAT (personal access token) is generated with `packages:read`,
update the `.zshrc`, `.bashrc`, or the default shell with the following:

```bash
export GITHUB_ACTOR="github-username"
export GITHUB_TOKEN="generated-personal-access-token"
```

The following will allow the app to install packages from GitHub Package registry

Note: the above configuration/PAT will allow installing not only maven but also npm
(amongst other) packages published to GitHub Packages

e.g. to install a package published to GitHub Packages npm registry, update the
`.npmrc` to have the following (where `@warnermedia` indicates the scope under
which a package is published, like the TypeScript implementation of the rules-engine
[here](https://github.com/WarnerMedia/Rules-Engine/pkgs/npm/rules-engine)):

```
@warnermedia:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken=${GITHUB_TOKEN}
```

## Relevant commands

Build the app/service

```bash
$ ./gradlew build
```

Run the app/service

```bash
$ ./gradlew run
```

## APIs

We will have two versions of the same API in the microservice.
The response would be a list of subscription plan IDs that the user
is eligible to purchase

- `/v1/subscription-eligibility`

    - Create the initial version of the eligibility API using traditional
    if-then-else approach

- `/v2/subscription-eligibility`

    - Create an updated version of the eligibility API using
[@warnermedia/rules-engine](https://github.com/WarnerMedia/Rules-Engine/packages/1679411)

Is there an exhaustive list of all the plans that users can be allowed to purchase?

Yes! This will be covered during the session but here it is:
- STANDARD_MONTHLY
- STANDARD_ANNUAL
- STUDENT_MONTHLY
- PREMIUM_MONTHLY
- DISCOUNTED_MONTHLY
