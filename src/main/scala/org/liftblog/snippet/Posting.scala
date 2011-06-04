package org.liftblog.snippet
import scala.xml._
import scala.collection.mutable.ListBuffer
import org.liftblog.model._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util.Helpers
import net.liftweb.http._
import Helpers._
import net.liftweb.mapper.{By, ByList}
import net.liftweb.textile.TextileParser
class Posting extends Logger {
	
	/**
	 * Creates PostTag ManyToMany relationships for specified post and tags.
	 * @param post - post to associate
	 * @param tags - tags
	 */
	def assocTags(post: Post, tags: Seq[String]) = {
		val tagsBuf = new ListBuffer[Tag]()
		for(tagText <- tags) {
			val tag: Tag = Tag.find(By(Tag.text, tagText)) match {
				case Full(tag) => tag
				case Empty => Tag.create.text(tagText)
				case f: Any => info("error occured" + f); S.redirectTo("/error")
			}
			tag.save
			PostTag.join(tag, post)
			tagsBuf += tag
		}
		tagsBuf.toList
	}
	/**
	 * Add and/or remove tags.
	 * @param post
	 * @param tags
	 */
	def editTagsFor(post: Post, tags: List[String]) = {
		val oldTags = PostTag.findAll(By(PostTag.post, post)).map(_.tag.obj).filter(_.isDefined).map(_.open_!)
		val newTags = assocTags(post, tags -- oldTags.map(_.text.is))
		val toremove = oldTags filterNot (Tag.findAll(ByList(Tag.text, tags)) contains);
		for(postTag <- PostTag.findAll(By(PostTag.post, post))) {
			postTag.tag.obj match {
				case Full(tag) if(toremove.contains(tag))=>  postTag.delete_!
				case _ =>
			}
		}
	}
	
		
	/** 
	 * Creates new post.
	 * @param in
	 * @return
	 */
	def add(in: NodeSeq): NodeSeq = {
		var postValues = Map[String,String]()
		var title = ""
		var text = ""
		var tags = ""
		def submit() = {
			List("en").foreach(lang => {
				val title = postValues(lang+"title").trim
				if(title=="") S.error("Title musn't be empty")
				else {
					val html = postValues(lang+"text") 
					val post = Post.create.date(new java.util.Date).text(html).title(title)
					post.save
					assocTags(post, tags.split(" "))
				}}
			)
			if(S.errors.isEmpty) S.redirectTo("/index")
		}
		List("en").flatMap(lang => bind("post",in,
				"lang" -> lang, 
				"title" -> SHtml.text("", parm => postValues += ((lang+"title",parm)), ("size","55")),
				"tags" -> SHtml.text("", parm => postValues += ((lang+"tags",parm))),
				"text" -> SHtml.textarea("", parm => postValues += ((lang+"text",parm)), ("id", "markitup"))
				
			)) ++ SHtml.submit("submit", submit)
	}
	
	def edit(in: NodeSeq): NodeSeq = {
		var title = ""
		var text = ""
		var post = Post.find(Index.postid)
		var tags = PostTag.findAll(By(PostTag.post, post.open_!)).
			 map(_.tag.obj).filter(_.isDefined).map(_.open_!.text.is).mkString(" ")
			
		def submit() = {
			if(title=="") S.error("Title musn't be empty") 
			else {
				val html = text 
				post.open_!.title(title).text(html).save
				editTagsFor(post.open_!, tags.split(" ").toList)
				S.redirectTo("/index")
			}
		}
		post match {
			case Full(p) => bind("post",in,
					"title" -> SHtml.text(p.title, parm => title=parm, ("size","55")),
					"tags" -> SHtml.text(tags, parm => tags=parm),
					"text" -> SHtml.textarea(p.text, parm => text=parm, ("id", "markitup")),
					"submit" -> SHtml.submit("Save", submit)
					)
			case Empty => S.error("Post to edit not found"); S.redirectTo("/index")
			case _ => S.error("Error occured"); S.redirectTo("/index")
		}
	}
}
