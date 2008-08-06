package org.liftblog.textile
import org.specs._
import net.liftweb.textile._
class TextileParserTest extends SpecificationWithJUnit {
	"parser should replace textile markup with html"  in {
		TextileParser.toHtml("*test*", false).toString.trim must_== (<p><strong>test</strong></p>).toString
	}
	
	"parser should trow and exception when input not well formed" in {
		TextileParser.toHtml("+ test italic").toString.trim must_== (<p>+ test italic</p>).toString
	}
	
	
}