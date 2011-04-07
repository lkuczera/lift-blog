package org.liftblog.model
import net.liftweb.mapper._
import net.liftweb.common._

class Property extends LongKeyedMapper[Property] with IdPK  {
	def getSingleton = Property
	object name extends MappedString(this,200){
		override def dbIndexed_? = true
	}
	object value extends MappedText(this)
	
	
}

object Property extends Property with LongKeyedMetaMapper[Property]{
	
	private val logger = Logger(classOf[Property])
	
	private def propertyFromResultBox(box:Box[Property], 
			                          propertyName:String,
								      defaultValue:String):Property = box match {
		case Full(prop) => prop //The property is found
		case Empty => {
			//Not found: create and init
			val prop = Property.create.name(propertyName).value(defaultValue) 
			prop.save()
			prop
		}
		case Failure(msg,exeption,_) => {
			//Database Error: Return Default and log error
			logger.error(msg)
			Property.create.name(propertyName).value(defaultValue)
		}
	}
	
	
	def title = {
		val name = "BLOG_TITLE"
		val defaultValue = "acidbits.org"
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}
	
	def subtitle = {
		val name = "BLOG_SUBTITLE"
		val defaultValue = "Acid bits of code :)"
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}
	
	def address = {
		val name = "ADDRESS"
		val defaultValue = "http://www.example.org"
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}
	
	def rssDescription = {
		val name = "RSS_DESCRIPTION"
		val defaultValue = "This is the description that will be published in the RSS feed."
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}

	def shortDescriptionTitle = {
		val name = "SHORT_DESCRIPTION_TITLE"
		val defaultValue = "Wise Words"
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}
	
	def shortDescription = {
		val name = "SHORT_DESCRIPTION"
		val defaultValue = """<p>&quot;To have a quiet mind is to 
			possess one's mind wholly; to have a calm spirit is to 
			possess one's self.&quot; </p>
					
			<p class="align-right">- Hamilton Mabie</p>"""
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}
	
	def metaKeywords = {
		val name = "META_KEYWORDS"
		val defaultValue = "these, are , the, keywords, that, will, be, published, in, the, meta, keywords, tag"
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}
	
	def metaDescription = {
		val name = "META_DESCRIPTION"
		val defaultValue = "This is the description that will be published in the meta description tag."
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}
	
	def copyrightNote = {
		val name = "COPYRIGHT_NOTE"
		val defaultValue = "© 2010 Lukasz Kuczera"
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}
	
	def theme = {
		val name = "THEME"
		val defaultValue = "coolwater"
			
		propertyFromResultBox(Property.find(By(Property.name, name)), 
			                          name,
								      defaultValue)
	}
	
	def themeNames = List("coolwater", "deviation")
	
}