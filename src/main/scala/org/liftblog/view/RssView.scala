package org.liftblog.view
import net.liftweb._
import scala.xml.NodeSeq
import http._
import common._
import util.Helpers._
//import java.util.Properties
import org.liftblog.model._
import mapper._
class RssView extends LiftView  {
	override def dispatch = {
		case "feed" => latest _ 
	}
	
	private def latest: NodeSeq = {
		val posts = Post.findAll(OrderBy(Post.date, Descending), MaxRows(20))
		val latestPostDate = posts match {
			case Nil => new java.util.Date
			case posts: List[Post] => posts.head.date.is
		}
		def bindItems = posts.flatMap(post =>
			bind("item",RssView.itemXml, "title" -> post.title,
					"description" -> post.text,
					"link" -> linkToPost(post.title),
					"date" -> post.date)
		)
		
		bind("rss", RssView.rssxml(bindItems), "lastBuildDate" -> latestPostDate.toString)
	}
	
	private def linkToPost(title: String) = "http://acidbits.org/blog"
			
	
}

object RssView extends Logger {
	// single item template
	val itemXml = <item>
				<title><item:title/></title>
				<description><item:description/></description>
				<link><item:link /></link>
				<guid isPermaLink="false"> 1102345</guid>
				<pubDate><item:date /></pubDate>
			</item>
	// renders whole rss provided items and static properties
	def rssxml(items: NodeSeq) = <rss version="2.0">
	<channel>
		<title>{Property.title.value /*props.getProperty("title")*/}</title>
			<description>{Property.rssDescription.value/*props.getProperty("descritpion")*/}</description>
			<link>{Property.address.value/*props.getProperty("link")*/}</link>
			<lastBuildDate><rss:lastBuildDate /></lastBuildDate>
			<pubDate>Tue, 29 Aug 2006 09:00:00 -0400</pubDate>
			{items}
	</channel>
</rss>

	/*lazy val props = {
		// ugly imperative style
		val props = new Properties
		try{ props.load(getClass.getResourceAsStream("/rss.properties"))} 
		catch  {
			case e: Exception => error(
			  "RSS feed accessed but no rss.properties found on the classpath")
		}
		props
	}*/
}