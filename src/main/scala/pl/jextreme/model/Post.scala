package pl.jextreme.model
import net.liftweb.mapper._

class Post extends LongKeyedMapper[Post] with IdPK  {
	def getSingleton = Post
	object text extends MappedText(this)
	object title extends MappedString(this,200)
	object lang extends MappedInt(this)
	object draft extends MappedBoolean(this)
	object date extends MappedDateTime(this)
	object userid extends MappedLongForeignKey(this,User)
}

object Post extends Post with LongKeyedMetaMapper[Post] with CRUDify[Long,Post]  {
	
}