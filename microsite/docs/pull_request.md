---
layout: docs
title: Pull Request API
permalink: pull_request
---

# Pull Request API

Github4s supports the [Pull Request API](https://developer.github.com/v3/pulls/). As a result,
with Github4s, you can interact with:

- [Pull requests](#pull-requests)
  - [Get a pull request](#get-a-pull-request)
  - [List pull requests](#list-pull-requests)
  - [List the files in a pull request](#list-the-files-in-a-pull-request)
  - [Create a pull request](#create-a-pull-request)
  - [Update branch](#update-a-pull-request-branch)
- [Reviews](#reviews)
  - [List reviews](#list-pull-request-reviews)
  - [Get a review](#get-an-individual-review)
  - [Create a review](#create-a-review)
- [Review requests](#review-requests)
  - [Add reviewers](#add-reviewers)
  - [List reviewers](#list-reviewers)
  - [Remove reviewers](#remove-reviewers)

The following examples assume the following code:

```scala mdoc:silent

import cats.effect.IO
import github4s.Github
import org.http4s.client.{Client, JavaNetClientBuilder}


val httpClient: Client[IO] = JavaNetClientBuilder[IO].create // You can use any http4s backend

val accessToken = sys.env.get("GITHUB_TOKEN")
val gh = Github[IO](httpClient, accessToken)
```

## Pull requests

### Get a pull request

You can get a single pull request for a repository using `get`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the pull request number

To get a single pull request:

```scala mdoc:compile-only
val getPullRequest = gh.pullRequests.getPullRequest("47degrees", "github4s", 102)
getPullRequest.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [PullRequest][pr-scala].

See [the API doc](https://developer.github.com/v3/pulls/#get-a-single-pull-request) for full reference.

### List pull requests

You can list the pull requests for a repository using `list`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- a list of [PRFilter](https://github.com/47degrees/github4s/blob/main/github4s/shared/src/main/scala/github4s/free/domain/PullRequest.scala).

As an example, let's say we want the open pull requests in <https://github.com/scala/scala> sorted
by popularity:

```scala mdoc:compile-only
import github4s.domain._
val prFilters = List(PRFilterOpen, PRFilterSortPopularity)
val listPullRequests = gh.pullRequests.listPullRequests("scala", "scala", prFilters)
listPullRequests.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the matching [List[PullRequest]][pr-scala].

See [the API doc](https://developer.github.com/v3/pulls/#list-pull-requests) for full reference.

### List the files in a pull request

You can also list the files for a pull request using `listFiles`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the pull request number.

To list the files for a pull request:

```scala mdoc:compile-only
val listPullRequestFiles = gh.pullRequests.listFiles("47degrees", "github4s", 102)
listPullRequestFiles.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

the `result` on the right is the [List[PullRequestFile]][pr-scala].

See [the API doc](https://developer.github.com/v3/pulls/#list-pull-requests-files) for full
reference.

### Create a pull request

If you want to create a pull request, we can follow two different methods.

On the one hand, we can pass the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `title` (as part of the `NewPullRequestData` object): Title for the pull request.
 - `body` (as part of the `NewPullRequestData` object): Description for the pull request.
 - `head`: The name of the branch where your changes are implemented.
 - `base`: The name of the branch you want the changes pulled into.
 - `maintainerCanModify`: Optional. Indicates whether maintainers can modify the pull request. `true` by default.

```scala mdoc:compile-only
import github4s.domain.NewPullRequestData

val createPullRequestData = gh.pullRequests.createPullRequest(
  "47deg",
  "github4s",
  NewPullRequestData("title", "body", draft = false),
  "my-branch",
  "base-branch",
  Some(true))
createPullRequestData.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

On the other hand, we can pass an `issue` id (through `NewPullRequestIssue` object)
instead of the title and body.

**NOTE**: This option deletes the issue.

```scala mdoc:compile-only
import github4s.domain.NewPullRequestIssue
val createPullRequestIssue = gh.pullRequests.createPullRequest(
  "47deg",
  "github4s",
  NewPullRequestIssue(105),
  "my-branch",
  "base-branch",
  Some(true))
createPullRequestIssue.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

See [the API doc](https://developer.github.com/v3/pulls/#create-a-pull-request) for full reference.

### Update a pull request branch

Merges the base HEAD into your pull request branch. 
Note that this is an experimental API, meaning github could stop supporting it at any time or change in an incompatible way. 

Accepts these parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `pullRequest`: integer id of you pr.
 - `expectedHeadSha`: The expected SHA of the pull request's HEAD ref for an optional check on github's side.

```scala mdoc:compile-only
import github4s.domain.BranchUpdateResponse

val updatePullRequestBranch = gh.pullRequests.updateBranch(
  "47deg",
  "github4s",
  567)  
updatePullRequestBranch.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

See [the API doc](https://developer.github.com/v3/pulls/#update-a-pull-request-branch) for full reference.

## Reviews

### List pull request reviews

You can list the reviews for a pull request using `listReviews`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the pull request id.

As an example, if we wanted to see all the reviews for pull request 139 of `47degrees/github4s`:

```scala mdoc:compile-only
val listReviews = gh.pullRequests.listReviews(
  "47deg",
  "github4s",
  139)
listReviews.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the matching [List[PullRequestReview]][pr-scala].

See [the API doc](https://developer.github.com/v3/pulls/reviews/#list-reviews-on-a-pull-request) for full reference.

### Get an individual review

You can get an individual review for a pull request using `getReview`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the pull request id.
- the review id.

As an example, if we wanted to see review 39355613 for pull request 139 of `47degrees/github4s`:

```scala mdoc:compile-only
val review = gh.pullRequests.getReview(
  "47deg",
  "github4s",
  139,
  39355613)
review.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the matching [PullRequestReview][pr-scala].

See [the API doc](https://developer.github.com/v3/pulls/reviews/#get-a-single-review) for full reference.

### Create a review

You can create a review for a pull request using `createReview`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the pull request id.
- `commit_id` (as part of the `CreatePRReviewRequest` object): The SHA of the commit that needs a review. Defaults to the most recent commit.
- `body` (as part of the `CreatePRReviewRequest` object): Required when using REQUEST_CHANGES or COMMENT for the event parameter. The body text of the pull request review.
- `event` (as part of the `CreatePRReviewRequest` object): The review action you want to perform. By leaving this blank, you set the review action state to PENDING.
- `comments` (as part of the `CreatePRReviewRequest` object): An optional list of draft review comments.

```scala mdoc:compile-only
import github4s.domain.{CreatePRReviewRequest, PRREventApprove}

val createReviewData = gh.pullRequests.createReview(
  "47deg",
  "github4s",
  139,
  CreatePRReviewRequest(Some("commit_id"), "body", PRREventApprove)  
)
createReviewData.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the created [PullRequestReview][pr-scala].

See [the API doc](https://developer.github.com/v3/pulls/reviews/#create-a-review-for-a-pull-request) for full reference.

## Review requests

This API allows you to operate on review requests. Reviewers can be users and/or teams. Usernames should be used without a leading '@' sign.

### Add reviewers

You can add reviewers for a pull request using `addReviewers`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the pull request id.
- users and teams you want to add as reviewers

As an example, if we wanted to add `torvalds` to the reviewers for pull request 139 of `47degrees/github4s`:

```scala mdoc:compile-only
import github4s.domain._
val addReviewers = gh.pullRequests.addReviewers(
  "47deg",
  "github4s",
  139,
  ReviewersRequest(List("torvalds")))
addReviewers.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the updated [PullRequest][pr-scala]. 

**NOTE**: you can't request a review from the pr's author. If your list of added reviewers contains the author, the whole request will be declined. 

See [the API doc](https://docs.github.com/en/free-pro-team@latest/rest/reference/pulls#request-reviewers-for-a-pull-request) for full reference.

### List reviewers

You can list the reviewers for a pull request using `listReviewers`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the pull request id.

As an example, if we wanted to see all the reviewers for pull request 139 of `47degrees/github4s`:

```scala mdoc:compile-only
val listReviewers = gh.pullRequests.listReviewers(
  "47deg",
  "github4s",
  139)
listReviewers.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the matching ReviewersResponse, which contains all users and teams, whose review has been requested. 

See [the API doc](https://docs.github.com/en/free-pro-team@latest/rest/reference/pulls#list-requested-reviewers-for-a-pull-request) for full reference.

### Remove reviewers

You can remove reviewers from a pull request using `removeReviewers`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the pull request id.
- users and teams you want to remove from reviewers

As an example, if we wanted to remove `torvalds` from the reviewers for pull request 139 of `47degrees/github4s`:

```scala mdoc:compile-only
import github4s.domain._
val removeReviewers = gh.pullRequests.removeReviewers(
  "47deg",
  "github4s",
  139,
  ReviewersRequest(List("torvalds")))
removeReviewers.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the updated [PullRequest][pr-scala]. 

See [the API doc](https://docs.github.com/en/free-pro-team@latest/rest/reference/pulls#remove-requested-reviewers-from-a-pull-request) for full reference.


As you can see, a few features of the pull request endpoint are missing. As a result, if you'd like
to see a feature supported, feel free to create an issue and/or a pull request!

[pr-scala]: https://github.com/47degrees/github4s/blob/main/github4s/src/main/scala/github4s/domain/PullRequest.scala
