package pl.jextreme.snippet

import pl.jextreme.model.{Post,Comment}
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

class Details {
	
	
	def show(in: NodeSeq): NodeSeq = {
		println("in details show")
		Post.find(Details.postid) match {
			case Full(post) => bind("post",in, 
				"title"->post.title, 
				"text" -> post.text,
				"date" -> (new SimpleDateFormat(Const.format) format post.date.get))
				
			case Empty => Text("No such post")
			case Failure(_,_,_) => S.redirectTo("/failure.html")
		}
	}
	
	def comments(in: NodeSeq): NodeSeq = {
		Comment.findAll(By(Comment.postid,Details.postid)).flatMap(comment =>
			bind("comment", in, "author" -> Text("dupa blada"),
					"text" -> comment.text,
					"date" -> comment.date)
		) 
	}
}

object Details {
	object postidVar extends RequestVar(S.param("postid").map(_.toLong) openOr 0L)
	def postid: Long = postidVar.is
}
