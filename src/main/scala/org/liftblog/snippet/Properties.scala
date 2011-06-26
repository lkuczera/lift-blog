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

  def edit = {

    /**
     * Only a redirect is necessary because we get/set the values within the render methods.
     */
    def process = {

      S.redirectTo("/edit_properties")
    }

    ".title" #> SHtml.text(Property.title.value,
                           Property.title.value(_).save(), ("size","55")) &
		".subtitle" #> SHtml.text(Property.subtitle.value,
                              Property.subtitle.value(_).save(), ("size","55")) &
		".address" #> SHtml.text(Property.address.value,
                             Property.address.value(_).save(), ("size","55")) &
		".rssDescription" #> SHtml.textarea(Property.rssDescription.value,
                                        Property.rssDescription.value(_).save(),("cols","55")) &
		".shortDescriptionTitle" #> SHtml.text(Property.shortDescriptionTitle.value,
                                           Property.shortDescriptionTitle.value(_).save(), ("size","55")) &
		".shortDescription" #> SHtml.textarea(Property.shortDescription.value,
                                          Property.shortDescription.value(_).save(),("cols","55")) &
		".metaKeywords" #> SHtml.textarea(Property.metaKeywords.value,
                                      Property.metaKeywords.value(_).save(),("cols","55")) &
		".metaDescription" #> SHtml.textarea(Property.metaDescription.value,
                                         Property.metaDescription.value(_).save(),("cols","55")) &
		".copyrightNote" #> SHtml.text(Property.copyrightNote.value,
                                   Property.copyrightNote.value(_).save(), ("size","55")) &
		".theme" #> (SHtml.select(Property.themeNames.map((t=>(t,t))), Full(Property.theme.value),
                              Property.theme.value(_).save()) ++ SHtml.hidden(() => process))
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
