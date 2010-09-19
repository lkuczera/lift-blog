package org.liftblog.model
import net.liftweb.mapper._

class Post extends LongKeyedMapper[Post] with IdPK  {
	def getSingleton = Post
	object text extends MappedText(this)
	object title extends MappedString(this,200)
	object lang extends MappedInt(this)
	object draft extends MappedBoolean(this)
	object date extends MappedDateTime(this)
	object userid extends MappedLongForeignKey(this,User)
	
	implicit def string2slash(str: String) = new SlashString(str)
	
	case class SlashString(val str: String) {
		def /(other: String) = str + "/" + other
	}
	
	
	def urlify = {
		val date = this.date.is
		val year = (date.getYear+1900).toString
		val month = date.getMonth+1
		val monthStr = if(month >9) month.toString else ("0"+month)
		year / monthStr / this.title
	}
}

object Post extends Post with LongKeyedMetaMapper[Post]