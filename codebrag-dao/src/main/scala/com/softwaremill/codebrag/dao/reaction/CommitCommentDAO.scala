package com.softwaremill.codebrag.dao.reaction

import com.softwaremill.codebrag.domain.{ThreadDetails, Like, Comment}
import org.bson.types.ObjectId

trait CommitCommentDAO {

  def save(comment: Comment)

  def findCommentsForCommits(commitId: ObjectId*): List[Comment]
  
  def findCommentsForFileInCommits(fileName: String, commitIds: Seq[ObjectId]) : List[Comment]

  def findAllCommentsForThread(thread: ThreadDetails): List[Comment]

}