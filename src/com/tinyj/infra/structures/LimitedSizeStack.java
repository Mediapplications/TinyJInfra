package com.tinyj.infra.structures;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/**
 * This datatype is intended to perform as a standard Stack, only with a limited size.
 * this means that if X is defined as the size of the Stack, only the latest X elements 
 * will be saved in the stack. older elements will be deleted.
 * 
 * the public methods comply exactly to the java.util.Stack class methods.
 * 
 * @author asaf.peeri
 *
 */
public class LimitedSizeStack<E>
{
	protected LinkedList<E> mInternalLinkedList = null;
	protected int mStackSizeLimit;
	protected boolean mLimitedSizeStack = false;
	protected E mLastRemovedItem = null;
		
	
	/**
	 * gets a defined size limit for this stack.
	 * if the aStackSizeLimit is equals or lower than 0, then this Stack WILL NOT be size limited.
	 * 
	 * @param aStackSizeLimit the size limit for this Stack
	 */
	public LimitedSizeStack(int aStackSizeLimit)
	{
		mInternalLinkedList = new LinkedList<E>();
		mStackSizeLimit = aStackSizeLimit;
		if (mStackSizeLimit > 0)
		{
			mLimitedSizeStack = true;
		}
	}
	
	/**
	 * Tests if this stack is empty.
	 * 
	 * @return true if and only if this stack contains no items; false otherwise.
	 */
	public boolean empty()
	{
		if (mInternalLinkedList.size() == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Looks at the object at the top of this stack without removing it from the stack.
	 * 
	 * @return the object at the top of this stack (the last item of the Vector object).
	 * 
	 * @throws EmptyStackException - if this stack is empty.
	 */
	public E peek()
		throws EmptyStackException
	{
		if (empty())
		{
			throw new EmptyStackException();
		}
		else
		{
			return mInternalLinkedList.getLast();
		}
	}
	
	
	/**
	 * Removes the object at the top of this stack and returns that object as the value of this function. 
	 *  
	 * @return The object at the top of this stack.
	 * 
	 * @throws EmptyStackException - if this stack is empty.
	 */
	public E pop()
	{
		E aboutToBePoppedItem = peek();
		mInternalLinkedList.removeLast();
		
		return aboutToBePoppedItem;
	}
	
	
	/**
	 * Pushes an item onto the top of this stack. if the number of items in the Stack increases
	 * more than the defined limit size, then the oldest item in the Stack is deleted.
	 * the last deleted item is available to be retrieved using the <i>getLastDeletedItem()</i>.
	 * 
	 * @param item - the item to be pushed onto this stack. 
	 * 
	 * @return the item argument. 
	 */
	public E push(E aItem)
	{		
		if (mLimitedSizeStack)
		{
			if (mInternalLinkedList.size() >= mStackSizeLimit)
			{
				mLastRemovedItem = mInternalLinkedList.removeFirst();
			}
		}
		
		mInternalLinkedList.addLast(aItem);
		
		return aItem;
	}
	
	
	/**
	 * Returns the 1-based position where an object is on this stack. If the object o occurs as an item in this stack, this method returns the distance from the top of the stack of the occurrence nearest the top of the stack; the topmost item on the stack is considered to be at distance 1. The equals method is used to compare o to the items in this stack. 
	 * 
	 * @param aObjToSearch - the desired object. 
	 * 
	 * @return the 1-based position from the top of the stack where the object is located; the return value -1 indicates that the object is not on the stack.
	 */
	public int search(Object aObjToSearch)
	{
		Iterator<E> iter = mInternalLinkedList.iterator();
		E currentItem = null;
		int currentIndex = 0; //initialized to 0 because of the 1-based position answer
		while (iter.hasNext())
		{
			currentItem = iter.next();
			currentIndex++;
			
			if (currentItem != null && currentItem.equals(aObjToSearch))
			{
				//we found the searched item, so return its index
				return currentIndex;
			}
		}
		
		//if we reached here, it means we didnt find the searched object
		return -1;
	}
	
	
	/**
	 * retrieves the last item that was deleted from the Stack. this refers only to items that were
	 * deleted as a cause of size limit excess. 
	 * @return
	 */
	public E getLastDeletedItem()
	{
		return mLastRemovedItem;
	}
	
	
	/**
	 * iterates all over the Stack items, and executes their own <i>toString()</i> method
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		Iterator<E> iter = mInternalLinkedList.iterator();
		E currentItem = null;
		sb.append("[");
		while (iter.hasNext())
		{
			currentItem = iter.next();
			sb.append("{");
			if (currentItem == null)
			{
				sb.append(" null ");
			}
			else
			{
				sb.append(currentItem.toString());
			}
			sb.append("};");
		}
		sb.append("]");
		
		return sb.toString();
	}
	
}
