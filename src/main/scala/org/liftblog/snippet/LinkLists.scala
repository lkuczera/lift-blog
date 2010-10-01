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
	
	//Link Lists Snippets
	
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
		<button onclick={RedirectTo("edit_link_list/" + list.id.is)}>Edit</button> 
		</td>
		<td style={if(list.position.is==0) "text-align: right;" else "text-align: left;"}>
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
		
	//Link List Snippets 	
	
	object currentLinkListRequestVar extends RequestVar[Box[LinkList]](Empty)
		
	def currentLinkList:Box[LinkList] = currentLinkListRequestVar.is match {
		case Empty => {
			val idString = S.param("link_list_id") openOr (return Empty)
			
			try{
				val linkList = LinkList.find(idString.toInt)
				currentLinkListRequestVar.set(linkList)
				
				linkList
			}catch{
				case _ => {
					Empty
				}
			}
		}
		case box => box
		
	}
	
	object currentLinkListItemRequestVar extends RequestVar[Box[LinkListItem]](Empty)
		
	def currentLinkListItem:Box[LinkListItem] = currentLinkListItemRequestVar.is match {
		case Empty => {
			try{
				val linkListItem = S.param("link_list_item_id") match {
					case Full(id) => {
						currentLinkListItemRequestVar.set(LinkListItem.find(id.toInt))
						currentLinkListItemRequestVar.is
					}
					case notFull => currentLinkList match{
						case Full(linkList) =>{
							//Return the first element
							linkList.items match {
								case first::rest => {
									currentLinkListItemRequestVar.set(Full(first))
									currentLinkListItemRequestVar.is
								}
								case Nil => Empty
							}
						}
						case _ => Empty
					}
				}
			
				linkListItem
				
			}catch{
				case _ => {
					Empty
				}
			}
		}
		case box => box
		
	}
		
	def addLinkListItemAjaxButton = currentLinkList match {
		case Full(linkList) => SHtml.ajaxButton("Add Item",() =>{
			
			linkList.addItem("New Item")
			if(linkList.items.length >1)
				SetHtml("LinkListItems", linkListItemsEditor)
			else SetHtml("LinkListItems", linkListItemsEditor) & SetHtml("LinkListItemEditor", editLinkListItemProperties)
		})
		case _ => S.redirectTo("/404.html")
		
	}
		
		
	def linkListItemsEditor: NodeSeq = currentLinkList match {
		case Full(linkList) => {
			
			val items = linkList.items
			
			<table>
			{
			items.flatMap((list)=>{
				linkListTableItem(list, items.length)
			})} 
			</table>
		}
		case _ => S.redirectTo("/404.html")
		
	}
	
	def linkListTableItem(item:LinkListItem, length:Int):NodeSeq =
		<tr id={"link_list_item_id_" + item.id.is} ><td><a href={item.address.is}>{item.title.is}</a></td>
		<td>
		{if((currentLinkListItem match {case Full(item)=>item.id.is;case _ => -1})==item.id.is)
			<b>Editing</b>
		 else 	SHtml.ajaxButton("Edit",() =>{
			 		currentLinkListItemRequestVar.set(Full(item))
			 
			 		SetHtml("LinkListItemEditor", editLinkListItemProperties) & 
			 		SetHtml("LinkListItems", linkListItemsEditor)
		 		})
		} 
		</td>
		<td style={if(item.position.is==0) "text-align: right;" else "text-align: left;"}>
		{if(item.position.is!=0)SHtml.ajaxButton(<xml:group>{Unparsed("&uArr;")}</xml:group>,() =>{
			item.moveUp()
			SetHtml("LinkListItems", linkListItemsEditor)
		})}
		{if(item.position.is!=length-1)SHtml.ajaxButton(<xml:group>{Unparsed("&dArr;")}</xml:group>,() =>{
			item.moveDown()
			SetHtml("LinkListItems", linkListItemsEditor)
		})}
		</td>
		<td>
		{SHtml.ajaxButton("Remove", () =>{
			Confirm("Do you realy want to delete the link list \"" + item.title.is + "\"?", 
					SHtml.ajaxInvoke(()=>{
						item.delete_!
						
						currentLinkListItemRequestVar.is match{
							case Full(currentItem) if(item.id.is== currentItem.id.is) =>{
								currentLinkListItemRequestVar.set(Empty)
								//Reset
								currentLinkListItem
							}
							case _ => currentLinkListItem
						}
						
						//JQuery14Artifacts.fadeOut("link_list_id_" + list.id.is, 400, 400)
						SetHtml("LinkListItems", linkListItemsEditor) &
						SetHtml("LinkListItemEditor", editLinkListItemProperties)
					})._2)	
		 })
		}</td></tr>
		
		
		
		def editLinkListProperties(in: NodeSeq): NodeSeq = currentLinkList match {
			case Full(linkList) => {
				//Title: <link_list:title /><br/>
				//Description: <link_list:description /><br/>
				//Show: <link_list:show /><br/>
				//<link_list:save /><br/>
			
				var title = linkList.title.is
				var description = linkList.description.is
				var show = linkList.show.is
			
				def processSave() = {
					
					linkList.title.set(title)
					linkList.description.set(description)
					linkList.show.set(show)
					linkList.save
					
					Alert("Successfuly Saved Changes")
				}
			
				SHtml.ajaxForm(
						bind("link_list", in,
								"title" -> SHtml.text(title, title = _, ("size", "47")),
								"description" -> SHtml.textarea(description, description = _, ("cols", "50")),
								"show" -> SHtml.checkbox(show, show = _),
								"save" -> SHtml.submit("Save", processSave _)
						) ++ SHtml.hidden(processSave _)
				)
			}
			case _ => S.redirectTo("/404.html")
		}
		
		
		def editLinkListItemPropertiesTemplate =	
			<div>
				Link Text: <link_list_item:title /><br/>
				Link Address: <link_list_item:address /><br/>
				Link Description: <br/>
				<link_list_item:description /><br/>
				<link_list_item:save /><br/>
	
			</div>
		
		def editLinkListItemProperties: NodeSeq =currentLinkListItem match {
			case Full(linkListItem) => {
				//Link Text: <link_list_item:title /><br/>
				//Link Address: <link_list_item:address /><br/>
				//<link_list_item:save /><br/>
			
				var title = linkListItem.title.is
				var address = linkListItem.address.is
				var description = linkListItem.description.is
				var linkListItemId = linkListItem.id.is.toString
			
				def processSave() = {
					
					currentLinkListItemRequestVar.set(currentLinkListItem)
							 	
					currentLinkListRequestVar.set(LinkList.find(By(LinkList.id,linkListItem.linkListId)))
					
					linkListItem.title.set(title)
					linkListItem.description.set(description)
					linkListItem.address.set(address)
					linkListItem.save
					
					currentLinkListItemRequestVar.set(Full(linkListItem))
					
					SetHtml("LinkListItems", linkListItemsEditor) &
					Alert("Successfuly Saved Changes")
					
				}
				
				SHtml.ajaxForm(bind("link_list_item", editLinkListItemPropertiesTemplate,
				     "title" -> SHtml.text(title, title = _, ("id", "link_list_item_title"), ("size", "47")),
					 "address" -> SHtml.text(address, address = _, ("id", "link_list_item_address"), ("size", "47")),
					 "description" -> SHtml.textarea(description, description = _, ("id", "link_list_item_description"), ("cols", "50")),
					 "save" -> SHtml.submit("Save", processSave _)
					) ++ SHtml.hidden(processSave _)
				)
			}
			case Empty => <div/>
			case _ => S.redirectTo("/404.html")
		}
		
		
		def frontPageLinkList(in: NodeSeq ): NodeSeq = {
				
			val linkLists = LinkList.findAll(By(LinkList.show,true),OrderBy(LinkList.position, Ascending))
		
			linkLists.flatMap((list)=>{
				bind("link_list", in,
								"title" -> list.title.is,
								"links" -> (list.items.flatMap((item)=>
									<li><a href={item.address.is} alt={item.description.is}>{item.title.is}</a></li>))
				)
			})
		} 
		
				

}
