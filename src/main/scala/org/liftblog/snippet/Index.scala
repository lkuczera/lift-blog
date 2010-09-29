package org.liftblog.snippet
import scala.xml._
import org.liftblog.model.{Post,User,Comment, Tag, PostTag}
import net.liftweb.mapper._
import java.text.SimpleDateFormat
import scala.xml.{NodeSeq, Text}
import net.liftweb._ 
import mapper._ 
import http._ 
import SHtml._ 
import util._ 
import common._
import Helpers._
import _root_.net.liftweb.http.js.{JE,JsCmd,JsCmds,Jx,jquery}
import JsCmds._ // For implicits
import JE._
import net.liftweb.http.js.jquery.JqJsCmds._ 
import net.liftweb.textile.TextileParser

class Index {
	private val strLength = 512
	/**
	 * Renders posts list.
	 */
	def post(in: NodeSeq): NodeSeq = {
		if(S.param("tag").isDefined) renderTag(in)
		else renderPosts(Post.findAll(OrderBy(Post.date, Descending)), in)
	}
	
	private[snippet] def renderTag(in: NodeSeq) = {
		val tag = S.param("tag").map(tag_? => Tag.find(By(Tag.text, tag_?))) openOr S.redirectTo("/404.html")
		val posts = PostTag.findAll(By(PostTag.tag,tag)).map(_.post.obj).filter(_.isDefined).map(_.open_!)
		renderPosts(posts, in)
	}
	
	private[snippet] def renderPosts(posts: Seq[Post], in: NodeSeq) = {
		// FIXME - Bug with malformed xml when post is cut down
		def shortenText(text: String) = text //if(text.length < strLength) text else text.substring(0, strLength)+" ..."
		//get number of comments for current post and bind html link in the view
		def commentsText(post: Post) = {val comments = (Comment findAll By(Comment.postid, post.id)).length
						Text("Comments(%d)".format((comments)))}
		
		posts.flatMap(post => bind("post",in, 
				"title"-> <a href={post.urlify}>{post.title}</a>, 
				"text" -> <xml:group>{Unparsed(shortenText(post.text))}</xml:group>,
				"date" -> (new SimpleDateFormat(Const.format) format post.date.get),
				"more" -> <a href={post.urlify} class="readmore">Read more</a> ,
				"comments" -> <a href={post.urlify+"#comments"} class="comments">{commentsText(post)}</a>,
				"edit" -> (if(User.loggedIn_?) SHtml.link("/edit", ()=>Index.postidVar(post.id), Text("Edit"), ("class","readmore"))
						   else Text(""))
				)
		)
	}


	
	/**
	 * Renders post in details.
	 * @param in
	 * @return
	 */
	def show(in: NodeSeq): NodeSeq = {
		val postTitle = S.param("title") openOr S.redirectTo("/404.html")
		
		Post.find(By(Post.title, postTitle)) match {
			case Full(post) => bind("post",in, 
				"title"->post.title, 
				"text" -> <xml:group>{Unparsed(post.text)}</xml:group>,
				"date" -> (new SimpleDateFormat(Const.format) format post.date.get))
				
			case Empty => Text("No such post")
			case _ => S.error("Error occured"); S.redirectTo("/index")
		}
	}
	
	/**
	 * Binds comments for related post.
	 */
	def comments(in: NodeSeq): NodeSeq = {
		val postTitle = S.param("title") openOr S.redirectTo("/404.html")
		val postid = Post find(By (Post.title, postTitle)) openOr S.redirectTo("/404.html")
		Comment.findAll(By(Comment.postid,postid)).flatMap(comment =>
			bind("comment", in, "author" -> <a href={comment.website}> {comment.author}</a>,
					"text" -> <xml:group>{Unparsed(comment.text)}</xml:group>,
					"date" -> (new SimpleDateFormat("E d MMM, HH:mm") format comment.date.get))
		) 
	}
	
	/**
	 * Creates ajax form for commenting
	 */
	def addComment(in: NodeSeq): NodeSeq = {
		var author = ""
		var text = ""
		var website = ""
		// new-comment is element on page inside 
		def onSubmit = {
				val now = new java.util.Date
				val html = TextileParser.toHtml(text, false).toString
				val c = Comment.create.author(author).text(html).postid(Index.postid).date(now).website(website)
				c.validate
				c.save
				AppendHtml("new-comment",(<p class="post-footer align-left">
				 <p style="margin-bottom: 5px; font-weight: bold;"><a href={website}> {author}</a> said...<br/></p>	
					<p>{Unparsed(html)}</p>
	    			<p>{now}</p> 					
	    		</p>))
			
		}
		
		ajaxForm(bind("comm", in, 
				"author" -> SHtml.text("", a =>author=a, ("id","comm-author")),
				"website" -> SHtml.text("", w =>website=w),
				"text" -%> SHtml.textarea("", t=>text=t, ("id","comm-text")),
				"submit" -> SHtml.ajaxSubmit("Post", ()=>onSubmit, ("class","button"))), Noop)
		
	}
	
	def allTags(in: NodeSeq): NodeSeq = {
		val tags = PostTag.findAll.map(_.tag.obj).filter(_.isDefined).map(_.open_!).removeDuplicates
		tags.map(tag => <a href={"/tag/"+tag.text} class="taglink" >{tag.text}</a>)
		
	}
	
}

object Index {
	
	object postidVar extends RequestVar(S.param("postid").map(_.toLong) openOr 0L)
	
	def postid: Long = postidVar.is

}
