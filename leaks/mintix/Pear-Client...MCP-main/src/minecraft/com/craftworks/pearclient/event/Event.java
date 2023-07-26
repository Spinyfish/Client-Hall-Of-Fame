package com.craftworks.pearclient.event;

import java.util.ArrayList;

public class Event {
	
	private boolean cancelled;
	
	public boolean isCancelled() {

        return cancelled;
    }

    public void setCancelled(boolean cancelled) {

        this.cancelled = cancelled;
    }

	
	public Event call() {

		final ArrayList<EventData> dataList = EventManager.get(this.getClass());

		if (dataList != null) {
			for (int i = 0; i < dataList.size(); ++i) {
				EventData data = dataList.get(i);
				
				try {
					data.target.invoke(data.source, this);
				}
				catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}

		return this;
	}	
}