package com.outlook.calendar.agenda;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ksachan on 7/6/17.
 */

public class OutlookAgendaEventList extends ArrayList<OutlookAgendaEventGroup>
{
	public OutlookAgendaEventList(int capacity)
	{
		super(capacity);
	}
	
	@Override
	public void add(int index, OutlookAgendaEventGroup group)
	{
		mChildrenSize += group.itemCount();
		super.add(index, group);
	}
	
	@Override
	public boolean add(OutlookAgendaEventGroup group)
	{
		mChildrenSize += group.itemCount();
		return super.add(group);
	}
	
	@Override
	public boolean addAll(Collection<? extends OutlookAgendaEventGroup> collection)
	{
		if (collection.isEmpty())
		{
			return false;
		}
		for (OutlookAgendaEventGroup group : collection)
		{
			add(group);
		}
		return true;
	}
	
	@Override
	public OutlookAgendaEventGroup remove(int index)
	{
		OutlookAgendaEventGroup group = super.remove(index);
		mChildrenSize -= group.itemCount();
		group.deactivate();
		return group;
	}
	
	@Override
	public void clear()
	{
		for (OutlookAgendaEventGroup group : this)
		{
			group.deactivate();
		}
		super.clear();
		mChildrenSize = 0;
	}
	
	int groupAndChildrenSize()
	{
		return size() + mChildrenSize;
	}
	
	public OutlookAgendaItem getGroupOrItem(int index)
	{
		int count = 0;
		for (int i = 0; i < size(); i++)
		{
			if (index < count + 1 + get(i).itemCount())
			{
				if (index == count)
				{
					return get(i);
				}
				else
				{
					return get(i).getItem(index - count - 1);
				}
			}
			else
			{
				count += 1 + get(i).itemCount();
			}
		}
		return null;
	}
	
	int mChildrenSize = 0;
}
