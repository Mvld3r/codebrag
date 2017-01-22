package com.softwaremill.codebrag.dao.reaction

import com.softwaremill.codebrag.dao.sql.SQLDatabase
import org.bson.types.ObjectId
import com.softwaremill.codebrag.domain.{Like, ThreadDetails}

class SQLLikeDAO(val database: SQLDatabase) extends LikeDAO with SQLReactionSchema {
  import database.driver.simple._
  import database._

  def save(like: Like) = db.withTransaction { implicit session =>
    likes += like
  }

  def findLikesForCommits(commitIds: ObjectId*): List[Like] = db.withTransaction { implicit session =>
    likes
      .filter(_.commitId inSet commitIds.toSet)
      .sortBy(_.postingTime.asc)
      .list()
  }

  def findLikesForFileInCommits(fileName: String, commitIds: Seq[ObjectId]): List[Like] = db.withTransaction { implicit session =>
    likes
      .filter(c => (c.commitId inSet commitIds.toSet) && c.fileName === fileName)
      .list()
  }

    
  def findAllLikesForThread(thread: ThreadDetails): List[Like] = db.withTransaction { implicit session =>
    likes
      .filter(c => c.commitId === thread.commitId && positionFilter(thread, c))
      .list()
  }

  def findById(likeId: ObjectId): Option[Like] = db.withTransaction { implicit session =>
    likes
      .filter(c => c.id === likeId)
      .firstOption()
  }

  def remove(likeId: ObjectId) {
    db.withTransaction { implicit session =>
      likes.filter(c => c.id === likeId).delete
    }
  }
}
