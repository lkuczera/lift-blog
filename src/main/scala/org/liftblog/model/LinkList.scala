package org.liftblog.model

import net.liftweb.mapper._
import net.liftweb.common._

class LinkList extends LongKeyedMapper[LinkList] with IdPK  {
	
	def getSingleton = LinkList
	
	object title extends MappedString(this,200)
	
	object description extends MappedText(this)
	
	object position extends MappedInt(this){
		override def dbIndexed_? = true
	}
	
	object userId extends MappedLongForeignKey(this,User)
	
	object show extends MappedBoolean(this) {
		override def dbColumnName = "nxshow1"
	}
	
	def items = 
		LinkListItem.findAll(By(LinkListItem.linkListId, this.id), 
				             OrderBy(LinkListItem.position, Ascending))		            
				             
	def moveUp() = move(-1)
	
	def moveDown() = move(1)
	
	def move(toRelativePosition:Int) = 
		LinkList.find(By(LinkList.position, this.position.is + toRelativePosition)) match {
			case  Full(item) =>{
				//Change position with item
				val thisPos = this.position.is
				position.set(item.position.is)
				item.position.set(thisPos)
				(this.save() && item.save())
			}
			case Empty => false
			case Failure(msg,_,_)=> {
				val logger = Logger(classOf[LinkListItem])
				logger.error("Could not move list item")
				false
			}
		}
	
	override def delete_!() = {

		val linkListsWithGreaterPositions = LinkList.findAll(By_>(LinkList.position, this.position.is))
		val logger = Logger(classOf[Property])
		for(list <- linkListsWithGreaterPositions){
			list.position.set(list.position.is-1)
			list.save()
		}

		super.delete_!
	} 
	
	def addItem(title:String) = {
		val newPos = 1 + (LinkListItem.findAll(By(LinkListItem.linkListId, this.id),OrderBy(LinkListItem.position, Descending), MaxRows(1)) match {
			case Nil => -1
			case last::rest => last.position.is 
		})
		
		LinkListItem.create.title(title).position(newPos).address("http://").linkListId(id.is).save()
	}

}

object LinkList extends LinkList with LongKeyedMetaMapper[LinkList]{
	
	def add(title:String) = {
		val newPos = 1 + (LinkList.findAll(OrderBy(LinkList.position, Descending), MaxRows(1)) match {
			case Nil => -1
			case last::rest => last.position.is	
		})
		
		LinkList.create.title(title + newPos).position(newPos).save()
	}
	
}