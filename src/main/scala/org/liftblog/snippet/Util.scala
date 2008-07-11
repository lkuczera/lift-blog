package org.liftblog.snippet
import scala.xml._
import org.liftblog.model.User

class Util {
	def loggedIn(in: NodeSeq): NodeSeq = Text("")
	
	def loggedOut(in: NodeSeq): NodeSeq = Text("not logged in")
}
