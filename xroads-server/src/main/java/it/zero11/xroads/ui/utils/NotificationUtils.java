package it.zero11.xroads.ui.utils;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class NotificationUtils {
	public enum NotificationType{
		NOTIFICATION,
		SUCCESS,
		WARNING,
		ERROR
	}

	public static void show(NotificationType type, String text) {
		Notification notification = new Notification();
		if (!type.equals(NotificationType.NOTIFICATION)) {
			notification.addThemeVariants(switch (type) {
			case NOTIFICATION -> null;
			case SUCCESS -> NotificationVariant.LUMO_SUCCESS;
			case WARNING, ERROR -> NotificationVariant.LUMO_ERROR;
			});
		}

		HorizontalLayout layout = new HorizontalLayout();
		layout.setAlignItems(FlexComponent.Alignment.CENTER);

		layout.add(switch (type) {
		case NOTIFICATION -> VaadinIcon.COMMENTS.create();
		case SUCCESS -> VaadinIcon.CHECK_CIRCLE.create();
		case WARNING -> VaadinIcon.WARNING.create();
		case ERROR -> VaadinIcon.CLOSE_CIRCLE.create();
		});

		layout.add(new Div(new Text(text)));
		
		if (type.equals(NotificationType.ERROR)) {
			Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(), clickEvent -> notification.close());
		    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		    layout.add(closeBtn);
		}
		
		
		notification.add(layout);

		notification.setPosition(Position.MIDDLE);
		notification.setDuration(switch (type) {
		case NOTIFICATION, SUCCESS -> 3000;
		case WARNING -> 5000;
		case ERROR -> 0;
		});

		notification.open();
	}
}
