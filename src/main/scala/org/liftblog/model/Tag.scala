package org.liftblog.model
import net.liftweb.mapper._

class Tag extends LongKeyedMapper[Tag] with IdPK{
	def getSingleton = Tag
	object text extends MappedPoliteString(this,100) {
		override def setFilter = {
			List(x=> x.trim) // trim before saving to db
		}
	}
}
object Tag extends Tag with LongKeyedMetaMapper[Tag]{
	override def fieldOrder=List(text)
}