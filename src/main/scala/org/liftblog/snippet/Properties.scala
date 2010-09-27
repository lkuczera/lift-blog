package org.liftblog.snippet

import scala.xml._
import org.liftblog.model._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util.Helpers
import net.liftweb.http._
import Helpers._
import net.liftweb.textile.TextileParser
import scala.xml._

class Properties {

	def edit(in: NodeSeq): NodeSeq = {

		
		var title = ""
		var subtitle = ""
		var copyrightNote = ""
		var theme = ""
		var address = ""
		var rssDescription = ""
		var shortDescriptionTitle = ""	
		var shortDescription = ""
	    var metaKeywords = ""
	    var metaDescription = ""
			
		def submit() = {
			Property.title.value(title).save()
			Property.subtitle.value(subtitle).save()
			Property.copyrightNote.value(copyrightNote).save()
			Property.theme.value(theme).save()
			Property.address.value(address).save()
			Property.rssDescription.value(rssDescription).save()
			Property.shortDescriptionTitle.value(shortDescriptionTitle).save()
			Property.shortDescription.value(shortDescription).save()
			Property.metaKeywords.value(metaKeywords).save()
			Property.metaDescription.value(metaDescription).save()
			
			S.redirectTo("/edit_properties")	
		}
		
		bind("property",in,
				"title" -> SHtml.text(Property.title.value, parm => title=parm, ("size","55")),
				"subtitle" -> SHtml.text(Property.subtitle.value, parm => subtitle=parm, ("size","55")),
				"address" -> SHtml.text(Property.address.value, parm => address=parm, ("size","55")),
				"rssDescription" -> SHtml.textarea(Property.rssDescription.value, parm => rssDescription=parm,("cols","55")),
				"shortDescriptionTitle" -> SHtml.text(Property.shortDescriptionTitle.value, parm => shortDescriptionTitle=parm, ("size","55")),
				"shortDescription" -> SHtml.textarea(Property.shortDescription.value, parm => shortDescription=parm,("cols","55")),
				"metaKeywords" -> SHtml.textarea(Property.metaKeywords.value, parm => metaKeywords=parm,("cols","55")),
				"metaDescription" -> SHtml.textarea(Property.metaDescription.value, parm => metaDescription=parm,("cols","55")),
				"copyrightNote" -> SHtml.text(Property.copyrightNote.value, parm => copyrightNote=parm, ("size","55")),
				"theme" -> SHtml.select(Property.themeNames.map((t=>(t,t))), Full(Property.theme.value), parm => theme=parm),
				"submit" -> SHtml.submit("Save", submit)
			)
			
	}
	
	def surroundWithSelectedTheme(in: NodeSeq): NodeSeq = 
		<lift:surround with={ Property.theme.value } at="content">
			{in}
		</lift:surround>
	
	
	
	def title: NodeSeq = Property.title.value.asHtml
	
	def titleLink: NodeSeq = <a href={Property.address.value}>{Property.title.value}</a>
	
	def subtitle: NodeSeq = <xml:group>{Unparsed(Property.subtitle.value)}</xml:group>
	
	def address: NodeSeq = Text(Property.address.value)
	
	def copyrightNote: NodeSeq = <xml:group>{Unparsed(Property.copyrightNote.value)}</xml:group>
	
	def shortDescriptionTitle: NodeSeq = Text(Property.shortDescriptionTitle.value)
	
	def shortDescription: NodeSeq = <xml:group>{Unparsed(Property.shortDescription.value)}</xml:group>
	
	def rssLink = 
	<link rel="alternate" type="application/rss+xml" title={Property.title.value} href={Text(Property.address.value + "/feed")} />
	
	def metaKeywordsTag: NodeSeq = 
		<meta name="keywords" content={ Property.metaKeywords.value } />
		
	def metaDescriptionTag: NodeSeq = 
		<meta name="description" content={ Property.metaDescription.value } />	
	
	
}