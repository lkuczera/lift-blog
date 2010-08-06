package pl.jextreme.snippet
import scala.xml._
import pl.jextreme.model._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util.Helpers
import net.liftweb.http._
import Helpers._
class Posting {
	
	/**
	 * Creates PostTag ManyToMany relationships for specified postid and tags.
	 * @param postid - id of the post
	 * @param tags - string containing space separated tags names 
	 */
	def assocTags(postid: Long, tags: String) = {}
	
	def add(in: NodeSeq): NodeSeq = {
		var title = ""
		var text = ""
		var tags = ""
		def submit() = {
			if(title=="") S.error("Title musn't be empty") 
			else {
				println(new java.util.Date)
				Post.create.date(new java.util.Date).text(text).title(title).save
				S.redirectTo("/index")
			}
		}
		bind("post",in,
				"title" -> SHtml.text("", parm => title=parm, ("size","55")),
				"tags" -> SHtml.text("", parm => tags=parm),
				"text" -> SHtml.textarea("", parm => text=parm, ("class", "wymeditor")),
				"submit" -> SHtml.submit("Submit", submit)
			)
	}
	
	def edit(in: NodeSeq): NodeSeq = {
		var title = ""
		var text = ""
		var tags = ""
		var post = Post.find(Index.postid)
			
		def submit() = {
			if(title=="") S.error("Title musn't be empty") 
			else {
				post.open_!.title(title).text(text).save
				S.redirectTo("/index")
			}
		}
		post match {
			case Full(p) => bind("post",in,
					"title" -> SHtml.text(p.title, parm => title=parm, ("size","55")),
					"tags" -> SHtml.text("", parm => tags=parm),
					"text" -> SHtml.textarea(p.text, parm => text=parm),
					"submit" -> SHtml.submit("Save", submit)
					)
			case Empty => S.error("Post to edit not found"); S.redirectTo("/index")
			case _ => S.error("Error occured"); S.redirectTo("/index")
		}
	}
}
