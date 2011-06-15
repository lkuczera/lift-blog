package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, StandardDBVendor}
import _root_.java.sql.{Connection, DriverManager}
import _root_.org.liftblog.model._
import org.apache.commons.io.IOUtils

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Logger {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
      
    }
    Schemifier.schemify(true, Schemifier.infoF _, User, Post, PostTag, Tag, Comment, Property, LinkList, LinkListItem)
    // create default user if none is present
    if(DB.runQuery("select * from users")._2.isEmpty) {
	    val res = this.getClass.getResourceAsStream("/basic.sql");
	    info("Crating basic database from classpath://basic.sql;")
	    // execute basic.sql script
	    DB.use(DefaultConnectionIdentifier)(con => 
	    	DB.prepareStatement(IOUtils.toString(res), con) { stmt => stmt.execute})
    }
    
    
    // where to search snippet
    LiftRules.addToPackages("org.liftblog")
    // checks if user is logged in
    val loggedIn = If(() => User.loggedIn_?,
              () => RedirectResponse("/user_mgt/login"))
    // Build SiteMap            
    val entries = 
    Menu(Loc("Home", List("index"), "Home")) ::
    Menu(Loc("Search",List("search"),"Search")) ::
    Menu(Loc("Properties",List("edit_properties"),"Properties", loggedIn)) ::
    Menu(Loc("EditLinkLists",List("edit_link_lists"),"Edit Link Lists", loggedIn)) ::
    Menu(Loc("Post",List("posting"),"Post to blog", loggedIn)) ::
    Menu(Loc("Edit",List("edit"),"Edit post", loggedIn, Hidden)) ::
    Menu(Loc("Details", List("details"), "Details", Hidden)) ::
    Menu(Loc("EditLinkList", List("edit_link_list") -> true, "Edit Link List", Hidden, loggedIn)) ::
    Menu(Loc("highlight", List("highlight"), "highlight", Hidden)) ::
    Menu(Loc("feed", List("feed"), "feed", Hidden)) ::
    Menu(Loc("RssView", List("RssView", "feed"), "RssView", Hidden)) ::
    User.sitemap

    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    S.addAround(DB.buildLoanWrapper)
    /** for H2 console  */
    LiftRules.passNotFoundToChain = true 
    LiftRules.liftRequest.append { 
    	case Req("static" :: _, _, _) => false 
    } 
    LiftRules.useXhtmlMimeType = false

    implicit def string2dateString(str: String): DateString = DateString(str)
    LiftRules.rewrite.append {
    	case RewriteRequest(ParsePath("edit_link_list" :: linkListId :: Nil,_,_,_),_,_)  => 
    		RewriteResponse(List("edit_link_list"), Map("link_list_id" -> linkListId))
    	case RewriteRequest(ParsePath("feed" :: Nil,_,_,_),_,_)  => RewriteResponse(List("RssView","feed"))
    	case RewriteRequest(ParsePath(year :: month :: post :: Nil,_,_, false),GetRequest,_) 
    		if(year.isYear &&  month.isMonth) => 
    			RewriteResponse(List("details"), Map("title" -> post, month -> month, year -> year))
    	case RewriteRequest(ParsePath("login" :: Nil,_,_,_),_,_)  => RewriteResponse("user_mgt" :: "login" :: Nil)
    	case RewriteRequest(ParsePath("tag" :: tag :: Nil ,_,_,_),_,_)
    		 => RewriteResponse("index" :: Nil, Map("tag" -> tag))
    }
    
    // 404.html handler
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (r @ req,failure) =>
      	println("Not found request:" + r)
        NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })

  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}

case class DateString(val str: String) {
	implicit def string2dateString(str: String): DateString = DateString(str)
	def isYear = str matches """\d\d\d\d"""
    def isMonth = str matches """\d\d"""
}
