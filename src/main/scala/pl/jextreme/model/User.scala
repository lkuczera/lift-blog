package pl.jextreme {
package model {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb.http.S
import scala.xml.Elem

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password, textArea)
  // disables automatic sign up form
//  override def createUserMenuLoc = Empty
  // comment this line out to require email validations
  override def skipEmailValidation = true
  // redefine login form for nice formatting
  override def loginXhtml=
	<lift:surround with="coolwater" at="content">
		  <form method="post" action={S.uri}>
		  <div style="margin-bottom: 5px; margin-left: 5px;"><strong>{S.??("log.in")}</strong></div>
		  <div style="float: left; line-height: 2.5em;margin-left: 5px; margin-right: 20px;">
		  	{S.??("email.address")}<br/>
		  	{S.??("password")}<br/>
		  	<a href={lostPasswordPath.mkString("/", "/", "")}>{S.??("recover.password")}</a>
          </div>
          <div style="line-height: 2.5em;">
          	<user:email /><br/>
          	<user:password /><br/>  
          	<user:submit />  
          </div>
     </form>
	</lift:surround>
  override def signupXhtml(user:User)=
	<lift:surround with="coolwater" at="content">
		{super.signupXhtml(user)}
	</lift:surround>
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server
  
  
  
  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
  
  
}

}
}
