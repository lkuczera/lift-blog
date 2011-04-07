package org.liftblog.model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common.Full

class GlobalConfiguration extends LongKeyedMapper[GlobalConfiguration] with IdPK {
	def getSingleton = GlobalConfiguration
	object chosenLanguages extends MappedLongForeignKey(this, Language) 
}

object GlobalConfiguration extends GlobalConfiguration with LongKeyedMetaMapper[GlobalConfiguration] {
}