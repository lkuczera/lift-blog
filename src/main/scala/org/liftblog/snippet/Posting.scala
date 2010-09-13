package org.liftblog.snippet
import scala.xml._
import org.liftblog.model._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util.Helpers
import net.liftweb.http._
import Helpers._
import net.liftweb.textile.TextileParser
class Posting {
	
	/**
	 * Creates PostTag ManyToMany relationships for specified postid and tags.
	 * @param postid - id of the post
	 * @param tags - string containing space separated tags names 
	 */
	def assocTags(postid: Long, tags: String) = {}
	
	/** 
	 * Creates new post.
	 * @param in
	 * @return
	 */
	def add(in: NodeSeq): NodeSeq = {
		var title = ""
		var text = ""
		var tags = ""
		def submit() = {
			if(title=="") S.error("Title musn't be empty") 
			else {
				val html =  text //TextileParser.toHtml(text, false).toString
				Post.create.date(new java.util.Date).text(html).title(title).save
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
				val html = text //TextileParser.toHtml(text, false).toString
				post.open_!.title(title).text(html).save
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
