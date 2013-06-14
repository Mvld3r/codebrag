package com.softwaremill.codebrag.service.comments

import com.softwaremill.codebrag.dao.CommitCommentDAO
import com.softwaremill.codebrag.domain.Comment
import com.softwaremill.codebrag.service.comments.command.{NewInlineCommitComment, NewEntireCommitComment, NewComment}
import pl.softwaremill.common.util.time.Clock
import org.bson.types.ObjectId

class CommentService(commentDao: CommitCommentDAO)(implicit clock: Clock) {

  def addCommentToCommit(command: NewComment): Comment = {
    val commentAsDomainObject = toDomainObject(command)
    commentDao.save(commentAsDomainObject)
    commentAsDomainObject
  }

  private def toDomainObject(newComment: NewComment): Comment = {
    newComment match {
      case c: NewEntireCommitComment => Comment(new ObjectId, c.commitId, c.authorId, clock.currentDateTimeUTC(), c.message)
      case c: NewInlineCommitComment => Comment(new ObjectId, c.commitId, c.authorId, clock.currentDateTimeUTC(), c.message, Some(c.fileName), Some(c.lineNumber))
    }
  }
}