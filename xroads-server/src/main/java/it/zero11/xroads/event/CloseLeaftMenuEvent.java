package it.zero11.xroads.event;

import com.github.appreciated.app.layout.component.menu.RoundImage;
import com.vaadin.flow.component.ComponentEvent;

public class CloseLeaftMenuEvent extends ComponentEvent<RoundImage> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CloseLeaftMenuEvent(RoundImage source, boolean fromClient) {
        super(source, fromClient);
    }
}