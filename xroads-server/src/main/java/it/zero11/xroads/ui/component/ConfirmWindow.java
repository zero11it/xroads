package it.zero11.xroads.ui.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import it.zero11.xroads.ui.i18n.UITranslation;

public class ConfirmWindow extends Dialog {
	private static final long serialVersionUID = 1L;
	
	public ConfirmWindow(String title, String message,
			final ComponentEventListener<ClickEvent<Button>> onConfirm,
			final ComponentEventListener<ClickEvent<Button>> onCancel) {
		setCloseOnEsc(false);
		setCloseOnOutsideClick(false);
		
		VerticalLayout windowLayout = new VerticalLayout();
		windowLayout.setSizeFull();
		windowLayout.setMaxWidth("30em");
		windowLayout.setSpacing(true);
		windowLayout.setMargin(false);
		windowLayout.setPadding(false);

		{
			windowLayout.add(new H3(title));

			Span label = new Span(message);
			windowLayout.add(label);
			windowLayout.setFlexGrow(1, label);

			HorizontalLayout buttons = new HorizontalLayout();
			buttons.setSizeUndefined();
			buttons.setSpacing(true);
			{
				Button cancel = new Button(getTranslation(UITranslation.ABSTRACT_COMPONENTS_BUTTON_CANCEL));
				cancel.addThemeName("abort");
				cancel.addClickListener((event) -> {
					ConfirmWindow.this.close();
					if (onCancel != null) {
						onCancel.onComponentEvent(event);
					}
				});
				buttons.add(cancel);
				
				Button confirm = new Button(getTranslation(UITranslation.ABSTRACT_COMPONENTS_BUTTON_CONFIRM));
				confirm.addClickListener((event) -> {
					ConfirmWindow.this.close();
					if (onConfirm != null) {
						onConfirm.onComponentEvent(event);
					}
				});
				buttons.add(confirm);
			}
			windowLayout.add(buttons);
		}

		add(windowLayout);
	}

	public static void askConfirm(String title, String message,
			final ComponentEventListener<ClickEvent<Button>> onConfirm,
			final ComponentEventListener<ClickEvent<Button>> onCancel){
		ConfirmWindow confirmWindow = new ConfirmWindow(title, message, onConfirm, onCancel);
		confirmWindow.open();
	}

	public static void askConfirm(String title, String message,
			final ComponentEventListener<ClickEvent<Button>> onConfirm){
		ConfirmWindow confirmWindow = new ConfirmWindow(title, message, onConfirm, null);
		confirmWindow.open();
	}
}
