package org.liftblog.snippet

import scala.xml._
import org.liftblog.model._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util.Helpers
import net.liftweb.http._
import Helpers._
import net.liftweb.textile.TextileParser
import scala.xml._

class Search {
	
	object seachResults extends RequestVar[List[Post]](Nil)
	object seachQuery extends RequestVar[String]("")
  
  def search = {
    var searchTerm = seachQuery.is
    
		def submit() = {
			seachQuery.set(searchTerm)
			
			if(searchTerm.trim.length == 0) {
				S.notice("No search query entered")
			} else{
				val searchWords = searchTerm.split("""\s""")
				val posts = Post.findAll
				val postsRankingNotSortedAll = posts.map((post) => {
            val allPostText = post.title.is + " " + post.text.is
            
            def numberOfOccurences(word:String) = allPostText.split(word).length - 1
            
            val exactMatchPattern = """.*""" + searchTerm + """.*"""
            val occurancis = searchWords.foldLeft(0) {
              (ranking, searchWord) => numberOfOccurences(searchWord) + ranking
            }
            
            val ranking = occurancis + (if(allPostText.matches(exactMatchPattern)) 100 else 0)
            
            (ranking, post)
          })
        
				val postsRankingNotSorted = postsRankingNotSortedAll.filter(_._1 > 0)
				val postsRankingSorted = postsRankingNotSorted.sortWith((e1, e2) => (e1._1 >= e2._1))
				val orderedResults = postsRankingSorted.map(_._2)
        
				seachResults.set(orderedResults)
			}
		}
    
    val bindSearchBox =
      "#term" #> SHtml.text(searchTerm, parm => searchTerm = parm) &
      ":submit" #> SHtml.submit("Search", submit)
     
     val bindResult = 
       seachResults.is match {
          case Nil =>
            "ol *" #> {
              if(searchTerm.length>0)
                "No results matching the search query found"
              else ""
            }
          case results =>
            "li *" #> (results flatMap {
              (post) => <a href={post.urlify}>{post.title}</a>
            })
        }
     bindSearchBox & bindResult
  }
}