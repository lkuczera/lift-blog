package org.liftblog.view
import net.liftweb._
import scala.xml.NodeSeq
import http._
import common._
import util.Helpers._
import java.util.Properties

class RssView extends LiftView  {
	override def dispatch = {
		case "feed" => latest _ 
	}
	
	private def latest = bind("item",RssView.render, "description" -> "my first rss",
		"title" -> "My Title")
			
	
}

object RssView extends Logger {
	val xml = <rss version="2.0">
<channel>
	<title>{props.getProperty("title")}</title>
		<description>{props.getProperty("descritpion")}</description>
		<link>{props.getProperty("link")}</link>
		<lastBuildDate>Mon, 28 Aug 2006 11:12:55 -0400 </lastBuildDate>
		<pubDate>Tue, 29 Aug 2006 09:00:00 -0400</pubDate>
		<item>
			<title>Item Example</title>
			<description>This is an example of an Item</description>
			<link>http://www.domain.com/link.htm</link>
			<guid isPermaLink="false"> 1102345</guid>
			<pubDate>Tue, 29 Aug 2006 09:00:00 -0400</pubDate>
		</item>
</channel>
</rss>

	def render: NodeSeq = xml

	val props = {
		// ugly imperative style
		val props = new Properties
		try{ props.load(getClass.getResourceAsStream("rss.properties"))} catch  {
			case e: Exception => error("RSS feed accessed but no rss.properties found on the classpath")
		}
		props
	}
}