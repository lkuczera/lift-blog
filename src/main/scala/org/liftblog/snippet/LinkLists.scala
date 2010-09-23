package org.liftblog.snippet

import scala.xml._
import org.liftblog.model._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util.Helpers
import net.liftweb.http._
import Helpers._
import net.liftweb.textile.TextileParser
import _root_.net.liftweb.http.js.JsCmds._
import net.liftweb.mapper._
import _root_.net.liftweb.http.js.jquery.JQuery14Artifacts 

class LinkLists{
	
	def addAjaxButton() = SHtml.ajaxButton("Add Link List",() =>{
		LinkList.add("New Link List") 
		SetHtml("LinkLists", linkListsEditor())
	})
	
	def linkListsEditor():NodeSeq = <table>{
		val linkLists = LinkList.findAll(OrderBy(LinkList.position, Ascending))
		
		linkLists.flatMap((list)=>{
			linkListsEditorItem(list, linkLists.length)
		})} 
	</table>
	
	def linkListsEditorItem(list:LinkList, length:Int):NodeSeq =
		<tr id={"link_list_id_" + list.id.is}><td>{list.title.is}</td>
		<td>
		{SHtml.ajaxButton("Edit",() =>{
			SetHtml("LinkLists", linkListsEditor())
		})}
		</td>
		<td>
		{if(list.position.is!=0)SHtml.ajaxButton(<xml:group>{Unparsed("&uArr;")}</xml:group>,() =>{
			list.moveUp()
			SetHtml("LinkLists", linkListsEditor())
		})}
		{if(list.position.is!=length-1)SHtml.ajaxButton(<xml:group>{Unparsed("&dArr;")}</xml:group>,() =>{
			list.moveDown()
			SetHtml("LinkLists", linkListsEditor())
		})}
		</td>
		<td>
		{SHtml.ajaxButton("Remove", () =>{
			Confirm("Do you realy want to delete the link list \"" + list.title.is + "\"?", 
					SHtml.ajaxInvoke(()=>{
						list.delete_!
						//JQuery14Artifacts.fadeOut("link_list_id_" + list.id.is, 400, 400)
						SetHtml("LinkLists", linkListsEditor())
					})._2)	
		 })
		}</td></tr>
		


}