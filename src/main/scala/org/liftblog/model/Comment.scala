package org.liftblog.model
import net.liftweb.mapper._
import net.liftweb.util._

class Comment  extends LongKeyedMapper[Comment] with IdPK {
	def getSingleton = Comment
	object postid extends MappedLongForeignKey(this,Post)
	object text extends MappedText(this)
	object author extends MappedPoliteString(this,128)
	object website extends MappedPoliteString(this,256)
	object date extends MappedDateTime(this)

}

object Comment extends Comment with LongKeyedMetaMapper[Comment]  