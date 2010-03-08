package pl.jextreme.snippet
import scala.xml._

import pl.jextreme.model.{Post,Comment,User}
import net.liftweb.mapper._
import java.text.SimpleDateFormat
import scala.xml._
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


class Index {
	private val strLength = 512
	/**
	 * Renders posts list.
	 */
	def post(in: NodeSeq): NodeSeq = {
		Post.findAll(OrderBy(Post.date, Descending)).flatMap(post => bind("post",in, 
				"title"->post.title, 
				"text" -> {if(post.text.length < strLength) post.text.get else post.text.substring(0, strLength)+" ..."},
				"date" -> (new SimpleDateFormat(Const.format) format post.date.get),
				"more" -> SHtml.link("/details.html",()=>Index.postidVar(post.id),Text("Read more"), ("class","readmore")),
				"comments" -> SHtml.link("/details.html", 
						()=>Index.postidVar(post.id), { 
						// get number of comments for current post and bind html link in the view
						val comments = (Comment findAll By(Comment.postid, post.id)).length
						Text("Comments(%d)".format((comments)))},  ("class","comments")),
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
		println("in details show")
		Post.find(Index.postid) match {
			case Full(post) => bind("post",in, 
				"title"->post.title, 
				"text" -> post.text,
				"date" -> (new SimpleDateFormat(Const.format) format post.date.get))
				
			case Empty => Text("No such post")
			case Failure(_,_,_) => S.redirectTo("/failure.html")
		}
	}
	
	/**
	 * Binds comments for related post.
	 */
	def comments(in: NodeSeq): NodeSeq = {
		Comment.findAll(By(Comment.postid,Index.postid)).flatMap(comment =>
			bind("comment", in, "author" -> <a href={comment.website}> {comment.author}</a>,
					"text" -> comment.text,
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
				val c = Comment.create.author(author).text(text).postid(Index.postid).date(now).website(website)
				c.validate
				c.save
				AppendHtml("new-comment",(<p class="post-footer align-left">
			 <div style="margin-bottom: 5px; font-weight: bold;"><a href={website}> {author}</a> said...<br/></div>	
				{text}
				<br/><br/>		
    			{now} 					
			</p>))
			
		}
		
		ajaxForm(bind("comm", in, 
				"author" -> SHtml.text("", a =>author=a, ("id","comm-author")),
				"website" -> SHtml.text("", w =>website=w),
				"text" -> SHtml.textarea("", t=>text=t, ("id","comm-text")),
				"submit" -> SHtml.ajaxSubmit("Post", ()=>onSubmit, ("class","button"))), Noop)
		
	}
	
}

object Index {
	object postidVar extends RequestVar(S.param("postid").map(_.toLong) openOr 0L)
	def postid: Long = postidVar.is
}
