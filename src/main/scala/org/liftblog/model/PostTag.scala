package org.liftblog.model
import net.liftweb.mapper._

class PostTag extends LongKeyedMapper[PostTag] with IdPK{
	def getSingleton=PostTag
	object tag extends MappedLongForeignKey(this,Tag)
	object post extends MappedLongForeignKey(this,Post)
}
object PostTag extends PostTag with LongKeyedMetaMapper[PostTag]{
	def join(tag:Tag,tx:Post) = this.create.tag(tag).post(tx).save
}