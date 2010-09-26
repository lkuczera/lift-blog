package org.liftblog.model

import net.liftweb.mapper._
import net.liftweb.common._

class LinkListItem extends LongKeyedMapper[LinkListItem] with IdPK  {
	
	def getSingleton = LinkListItem
	
	object title extends MappedString(this,200)
	
	object address extends MappedString(this,200)
	
	object description extends MappedText(this)
	
	object linkListId extends MappedLongForeignKey(this,LinkList){
		override def dbNotNull_? = true
	}
	
	object position extends MappedInt(this){
		override def dbIndexed_? = true
	}
	
	def moveUp() = move(-1)
	
	def moveDown() = move(1)
	
	def move(toRelativePosition:Int) = 
		LinkListItem.find(By(LinkListItem.linkListId, this.linkListId),By(LinkListItem.position, this.position.is + toRelativePosition)) match {
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
		val linkListItemsWithGreaterPositions = LinkListItem.findAll(By(LinkListItem.linkListId, this.linkListId),By_>(LinkListItem.position, this.position.is))
		 
		for(listItem <- linkListItemsWithGreaterPositions){
			listItem.position.set(listItem.position.is-1)
			listItem.save()
		}
		
		super.delete_!
			
	}
	
}

object LinkListItem extends LinkListItem with LongKeyedMetaMapper[LinkListItem]
	 
