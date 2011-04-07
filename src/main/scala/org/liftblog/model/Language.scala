package org.liftblog.model
import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common.Full

class Language extends LongKeyedMapper[Language] with IdPK {
	def getSingleton = Language
	object chosenLanguages extends MappedLanguageList(this)
	object countryCode extends MappedString(this, 5)
	object countryName extends MappedString(this, 60)
}

object Language extends Language with LongKeyedMetaMapper[Language] {
}

class MappedLanguageList[T <: Mapper[T]](towner: T) extends MappedString(towner, 256) {
	def getLanguages = this.is.split(" ").toList
	def apply(langs: List[String]):T = super.apply(Full(langs.mkString(" ")))
}