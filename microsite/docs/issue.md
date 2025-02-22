---
layout: docs
title: Issue API
permalink: issue
---

# Issue API

Github4s supports the [Issue API](https://developer.github.com/v3/issues/). As a result,
with Github4s, you can interact with:

- [Issues](#issues)
  - [Create an issue](#create-an-issue)
  - [Edit an issue](#edit-an-issue)
  - [List issues](#list-issues)
  - [Get a single issue](#get-a-single-issue)
  - [Search issues](#search-issues)
- [Comments](#comments)
  - [List comments](#list-comments)
  - [Create a comment](#create-a-comment)
  - [Edit a comment](#edit-a-comment)
  - [Delete a comment](#delete-a-comment)
- [Labels](#labels)
  - [List labels for this repository](#list-labels-for-this-repository)
  - [Create a label](#create-a-label)
  - [Update a label](#update-a-label)
  - [Delete a label](#delete-a-label)
  - [List labels](#list-labels)
  - [Add labels](#add-labels)
  - [Remove a label](#remove-a-label)
- [Assignees](#assignees)
  - [List available assignees](#list-available-assignees)
- [Milestones](#milestones)
  - [List milestones for a respository](#list-milestones-for-a-repository)
  - [Get a single milestone](#get-a-single-milestone)
  - [Create milestone](#create-milestone)
  - [Update a milestone](#update-a-milestone)
  - [Delete a milestone](#delete-a-milestone)

The following examples assume the following code:

```scala mdoc:silent

import cats.effect.IO
import github4s.Github
import github4s.domain.Label
import org.http4s.client.{Client, JavaNetClientBuilder}


val httpClient: Client[IO] = JavaNetClientBuilder[IO].create // You can use any http4s backend

val accessToken = sys.env.get("GITHUB_TOKEN")
val gh = Github[IO](httpClient, accessToken)
```

## Issues

### Create an issue

You can create an issue using `createIssue`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the content of the issue (`title` and `body`).
- other optional parameters: `milestone id`, `labels` and `assignees` which are only taken into account
if you have push access to the repository.

To create an issue:

```scala mdoc:compile-only
val createIssue =
  gh.issues.createIssue("47degrees", "github4s", "Github4s", "is awesome", None, List("Label"), List("Assignee"))
createIssue.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the created [Issue][issue-scala].

See [the API doc](https://developer.github.com/v3/issues/#create-an-issue) for full reference.


### Edit an issue

You can edit an existing issue using `editIssue`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- the issue `number`.
- the updated `state` of the issue (open or closed).
- the edited `content` of the issue (title and body).
- other optional parameters: `milestone id`, `labels` and `assignees` which are only taken into account
if you have push access to the repository.

To edit an issue:

```scala mdoc:compile-only
val editIssue =
  gh.issues.editIssue("47degrees", "github4s", 1, "open", "Github4s", "is still awesome", None, List("Label"), List("Assignee"))
editIssue.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

the `result` on the right is the edited [Issue][issue-scala].

See [the API doc](https://developer.github.com/v3/issues/#edit-an-issue) for full reference.


### List issues

You can also list issues for a repository through `listIssues`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- `pagination`: Limit and Offset for pagination, optional.

To list the issues for a repository:

```scala mdoc:compile-only
val listIssues = gh.issues.listIssues("47degrees", "github4s")
listIssues.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [List[Issue]][issue-scala]. Note that it will
contain pull requests as Github considers pull requests as issues.

See [the API doc](https://developer.github.com/v3/issues/#list-issues-for-a-repository)
for full reference.

### Get a single issue

You can also get a single issue of a repository through `getIssue`; it takes as arguments:

- the repository coordinates (`owner` and `name` of the repository).
- `number`: The issue number.

To get a single issue from a repository:

```scala mdoc:compile-only
val issue = gh.issues.getIssue("47degrees", "github4s", 17)
issue.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [Issue][issue-scala]. Note that it will
return pull requests as Github considers pull requests as issues.

See [the API doc](https://developer.github.com/v3/issues/#get-a-single-issue)
for full reference.

### Search issues

Lastly, you can also search issues all across Github thanks to `searchIssues`; it takes as
arguments:

- a `query` string (the URL encoding is taken care of by Github4s).
- a list of [SearchParam](https://github.com/47degrees/github4s/blob/main/github4s/shared/src/main/scala/github4s/free/domain/SearchParam.scala).

Let's say we want to search for the Scala bugs (<https://github.com/scala/bug>) which contain
the "existential" keyword in their title:

```scala mdoc:compile-only
import github4s.domain._
val searchParams = List(
  OwnerParamInRepository("scala/bug"),
  IssueTypeIssue,
  SearchIn(Set(SearchInTitle))
)
val searchIssues = gh.issues.searchIssues("existential", searchParams)
searchIssues.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is a [SearchIssuesResult][issue-scala].

See [the API doc](https://developer.github.com/v3/search/#search-issues) for full reference.

## Comments

### List comments

You can list comments of an issue with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `number`: The issue number.
 - `pagination`: Limit and Offset for pagination, optional.

 To list comments:

```scala mdoc:compile-only
val commentList = gh.issues.listComments("47degrees", "github4s", 17)
commentList.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [List[Comment]][issue-scala]

See [the API doc](https://developer.github.com/v3/issues/comments/#list-comments-on-an-issue) for full reference.

### Create a comment

You can create a comment for an issue with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `number`: The issue number.
 - `body`: The comment description.

 To create a comment:

```scala mdoc:compile-only
val createcomment = gh.issues.createComment("47degrees", "github4s", 17, "this is the comment")
createcomment.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is a [Comment][issue-scala].

See [the API doc](https://developer.github.com/v3/issues/comments/#create-a-comment) for full reference.


### Edit a comment

You can edit a comment from an issue with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `id`: The comment id.
 - `body`: The new comment description.

 To edit a comment:

```scala mdoc:compile-only
val editComment = gh.issues.editComment("47degrees", "github4s", 20, "this is the new comment")
editComment.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is a [Comment][issue-scala].

See [the API doc](https://developer.github.com/v3/issues/comments/#edit-a-comment) for full reference.


### Delete a comment

You can delete a comment from an issue with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `id`: The comment id.

 To delete a comment:

```scala mdoc:compile-only
val deleteComment = gh.issues.deleteComment("47degrees", "github4s", 20)
deleteComment.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is `Unit`.

See [the API doc](https://developer.github.com/v3/issues/comments/#delete-a-comment) for full reference.

## Labels

### List labels for this repository

You can list labels for an issue with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `pagination`: Limit and Offset for pagination, optional.

 To list labels:

```scala mdoc:compile-only
val labelListRepository = gh.issues.listLabelsRepository("47degrees", "github4s")
labelListRepository.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [List[Label]][issue-scala]

See [the API doc](https://developer.github.com/v3/issues/labels/#list-all-labels-for-this-repository) for full reference.

### Create a label

You can create label for an repository with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `label`: Label for create (`name`, `color` and optional field `desctiption`) 

 To create label:

```scala mdoc:compile-only
val label = Label(
    name = "bug",
    color = "ffffff",
    id = None,
    description = None,
    url = None,
    default = None
)
val createLabel = gh.issues.createLabel("47degrees", "github4s", label)
createLabel.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding created [Label][issue-scala]

See [the API doc](https://developer.github.com/v3/issues/labels/#create-a-label) for full reference.

### Update a label

You can update existing label for an repository with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `label`: Label for update (`name`, `color` and optional field `desctiption`) 

 To update label:

```scala mdoc:compile-only
val label = Label(
    name = "bug",
    color = "ffffff",
    id = None,
    description = None,
    url = None,
    default = None
)
val updateLabel = gh.issues.updateLabel("47degrees", "github4s", label)
updateLabel.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding updated [Label][issue-scala]

See [the API doc](https://developer.github.com/v3/issues/labels/#update-a-label) for full reference.

### Delete a label

You can delete existing label for an repository with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `label`: The existing label name

 To delete label:

```scala mdoc:compile-only
val deleteLabel = gh.issues.deleteLabel("47degrees", "github4s", "bug")
deleteLabel.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is `Unit`.

See [the API doc](https://developer.github.com/v3/issues/labels/#delete-a-label) for full reference.

### List labels

You can list labels for an issue with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `number`: The issue number.
 - `pagination`: Limit and Offset for pagination, optional.

 To list labels:

```scala mdoc:compile-only
val labelList = gh.issues.listLabels("47degrees", "github4s", 17)
labelList.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [List[Label]][issue-scala]

See [the API doc](https://developer.github.com/v3/issues/labels/#list-labels-on-an-issue) for full reference.

### Add labels

You can add existing labels to an issue with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `number`: The issue number.
 - `labels`: The existing labels that require adding.

 To add existing labels to an issue:

```scala mdoc:compile-only
val assignedLabelList = gh.issues.addLabels("47degrees", "github4s", 17, List("bug", "code review"))
assignedLabelList.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding assigned [List[Label]][issue-scala]

See [the API doc](https://developer.github.com/v3/issues/labels/#add-labels-to-an-issue) for full reference.

### Remove a label

You can remove a label from an issue with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `number`: The issue number.
 - `label`: The label that requires removing.

 To remove an existing label from an issue:

```scala mdoc:compile-only
val removedLabelList = gh.issues.removeLabel("47degrees", "github4s", 17, "bug")
removedLabelList.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding removed [List[Label]][issue-scala]

See [the API doc](https://developer.github.com/v3/issues/labels/#remove-a-label-from-an-issue) for full reference.

## Assignees

### List available assignees

You can list available assignees for issues in repo with the following parameters:

 - the repository coordinates (`owner` and `name` of the repository).
 - `pagination`: Limit and Offset for pagination, optional.

 To list available assignees:

```scala mdoc:compile-only
val assignees = gh.issues.listAvailableAssignees("47degrees", "github4s")
assignees.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [List[User]][user-scala]

See [the API doc](https://developer.github.com/v3/issues/assignees/#list-assignees) for full reference.

As you can see, a few features of the issue endpoint are missing.

As a result, if you'd like to see a feature supported, feel free to create an issue and/or a pull request!

[issue-scala]: https://github.com/47degrees/github4s/blob/main/github4s/src/main/scala/github4s/domain/Issue.scala
[user-scala]: https://github.com/47degrees/github4s/blob/main/github4s/src/main/scala/github4s/domain/User.scala

## Milestones

### List milestones for a repository

You can list the milestone for a particular organization and repository with `listMilestones`; it takes arguments:

 - `owner`: name of the owner for which we want to retrieve the milestones.
 - `repo`: name of the repository for which we want to retrieve the milestones.
 - `state`: filter projects returned by their state. Can be either `open`, `closed`, `all`. Default: `open`, optional
 - `sort`: what to sort results by. Either `due_on` or `completeness`. Default: `due_on`, optional
 - `direction` the direction of the sort. Either `asc` or `desc`. Default: `asc`, optional
 - `pagination`: Limit and Offset for pagination, optional.
 - `header`: headers to include in the request, optional.

 To list the milestone for owner `47deg` and repository `github4s`:

```scala mdoc:compile-only
val milestones = gh.issues.listMilestones("47degrees", "github4s", Some("open"), None, None)
milestones.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [List[Milestone]][milestone-scala]

See [the API doc](https://developer.github.com/v3/issues/milestones/#list-milestones-for-a-repository) for full reference.

[milestone-scala]: https://github.com/47degrees/github4s/blob/main/github4s/src/main/scala/github4s/domain/Milestone.scala

### Create milestone

You can create a milestone for a particular organization and repository with `createMilestone`; it takes arguments:

 - `owner`: name of the owner for which we want to create the milestones.
 - `repo`: name of the repository for which we want to create the milestones.
 - `state`: The state of the milestone. Either `open` or `closed`. Default: `open`, optional
 - `title`: The title of the milestone.
 - `description`: A description of the milestone, optional
 - `due_on`: The milestone due date. This is a timestamp in ISO 8601 format: `YYYY-MM-DDTHH:MM:SSZ`, optional.
 - `header`: headers to include in the request, optional.

 To create a milestone for owner `47deg` and repository `github4s`:

```scala mdoc:compile-only
val milestone = gh.issues.createMilestone("47degrees", "github4s", "New milestone",Some("open"), None, None)
milestone.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [Milestone][milestone-scala]

See [the API doc](https://developer.github.com/v3/issues/milestones/#create-a-milestone) for full reference.

[milestone-scala]: https://github.com/47degrees/github4s/blob/main/github4s/src/main/scala/github4s/domain/Milestone.scala

### Get a single milestone

You can also get a single milestone of a repository through `getMilestone`; it takes as arguments:

- `owner`: name of the owner for which we want to retrieve the milestones.
- `repo`: name of the repository for which we want to retrieve the milestones.
- `number`: The milestone number.
- `header`: headers to include in the request, optional.

 To get milestone number 3254 for owner `47deg` and repository `github4s`:

```scala mdoc:compile-only
val milestone = gh.issues.getMilestone("47degrees", "github4s", 32)
milestone.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [Milestone][milestone-scala]

See [the API doc](https://developer.github.com/v3/issues/milestones/#get-a-single-milestone) for full reference.

[milestone-scala]: https://github.com/47degrees/github4s/blob/main/github4s/src/main/scala/github4s/domain/Milestone.scala

### Update a milestone

You can update a milestone for a particular organization and repository with `updateMilestone`; it takes arguments:

 - `owner`: name of the owner for which we want to create the milestones.
 - `repo`: name of the repository for which we want to create the milestones.
 - `milestone_number`: number of milestone.
 - `state`: The state of the milestone. Either `open` or `closed`. Default: `open`, optional
 - `title`: The title of the milestone.
 - `description`: A description of the milestone, optional
 - `due_on`: The milestone due date. This is a timestamp in ISO 8601 format: `YYYY-MM-DDTHH:MM:SSZ`, optional.
 - `header`: headers to include in the request, optional.

 To update a milestone for owner `47deg` and repository `github4s`:

```scala mdoc:compile-only
val milestone = gh.issues.updateMilestone("47degrees", "github4s", 1 , "New milestone", Some("open"), None, None)
milestone.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is the corresponding [Milestone][milestone-scala]

See [the API doc](https://developer.github.com/v3/issues/milestones/#update-a-milestone) for full reference.

[milestone-scala]: https://github.com/47degrees/github4s/blob/main/github4s/src/main/scala/github4s/domain/Milestone.scala

### Delete a milestone

You can delete a milestone for a particular organization and repository with `deleteMilestone`; it takes arguments:

 - `owner`: name of the owner for which we want to create the milestones.
 - `repo`: name of the repository for which we want to create the milestones.
 - `milestone_number`: number of milestone
 - `header`: headers to include in the request, optional.

 To delete a milestone for owner `47deg` and repository `github4s`:

```scala mdoc:compile-only
val milestone = gh.issues.deleteMilestone("47degrees", "github4s", 1)
milestone.flatMap(_.result match {
  case Left(e)  => IO.println(s"Something went wrong: ${e.getMessage}")
  case Right(r) => IO.println(r)
})
```

The `result` on the right is `Unit`.

See [the API doc](https://developer.github.com/v3/issues/milestones/#delete-a-milestone) for full reference.
