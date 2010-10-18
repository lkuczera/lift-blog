package org.liftblog.model
import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common.Full

class Language extends LongKeyedMapper[Languages] with IdPK {
	def getSingleton = Languages
	object chosenLanguages extends MappedLanguageList(this)
	object countryCode extends MappedString(this, 5)
}

object Language extends Language with LongKeyedMetaMapper[Language] {
	def getAll = find
}

class MappedLanguageList[T <: Mapper[T]](towner: T) extends MappedString(towner, 256) {
	def getLanguages = this.is.split(" ").toList
	def apply(langs: List[String]):T = super.apply(Full(langs.mkString(" ")))
}