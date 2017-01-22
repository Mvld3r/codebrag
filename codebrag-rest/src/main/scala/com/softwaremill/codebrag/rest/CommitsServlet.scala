package com.softwaremill.codebrag.rest

import org.scalatra._
import com.softwaremill.codebrag.service.user.Authenticator
import json.JacksonJsonSupport

import com.softwaremill.codebrag.service.diff.DiffWithCommentsService
import com.softwaremill.codebrag.service.comments.UserReactionService
import com.softwaremill.codebrag.dao.user.UserDAO
import com.softwaremill.codebrag.dao.finders.reaction.ReactionFinder
import com.softwaremill.codebrag.finders.commits.toreview.{ToReviewCommitsViewBuilder, ToReviewCommitsFinder}
import com.softwaremill.codebrag.finders.commits.all.AllCommitsFinder
import com.softwaremill.codebrag.usecases.reactions._
import com.softwaremill.codebrag.service.browser.BrowseService

class CommitsServlet(val authenticator: Authenticator,
                     val toReviewCommitsViewBuilder: ToReviewCommitsViewBuilder,
                     val reviewableCommitsListFinder: ToReviewCommitsFinder,
                     val allCommitsFinder: AllCommitsFinder,
                     val reactionFinder: ReactionFinder,
                     val addCommentUseCase: AddCommentUseCase,
                     val reviewCommitUseCase: ReviewCommitUseCase,
                     val reviewAllCommitsUseCase: ReviewAllCommitsUseCase,
                     val userReactionService: UserReactionService,
                     val userDao: UserDAO,
                     val diffService: DiffWithCommentsService,
                     val unlikeUseCase: UnlikeUseCase,
                     val likeUseCase: LikeUseCase,
                     val browseService : BrowseService)
  extends JsonServletWithAuthentication with JacksonJsonSupport with CommitsEndpoint with CommentsEndpoint with LikesEndpoint {
}

object CommitsServlet {
  val MAPPING_PATH = "commits"
}

