package pl.jextreme.snippet
import scala.xml._
import pl.jextreme.model.User

class Util {
	def loggedIn(in: NodeSeq): NodeSeq = Text("")
	
	def loggedOut(in: NodeSeq): NodeSeq = Text("not logged in")
}
