package it.zero11.rewix.sync.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VerticalScrollLayout extends Div {
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout content;

	public VerticalScrollLayout(){
		preparePanel();
	}

	public VerticalScrollLayout(Component... children){
		preparePanel();
		this.add(children);
	}

	private void preparePanel() {
		setWidth("100%");
		setHeight("100%");
		getStyle().set("overflow", "auto");

		content = new VerticalLayout();
		//content.getStyle().set("display", "block");
		content.setWidth("100%");
		super.add(content);
	}

	public VerticalLayout getContent(){
		return content;
	}

	@Override
	public void add(Component... components){
		content.add(components);
	}

	@Override
	public void remove(Component... components){
		content.remove(components);
	}

	@Override
	public void removeAll(){
		content.removeAll();
	}

	@Override
	public void addComponentAsFirst(Component component) {
		content.addComponentAtIndex(0, component);
	}

	@Override
	public void addComponentAtIndex(int index, Component component){
		content.addComponentAtIndex(index, component);
	}
	
    public void setSpacing(boolean spacing) {
    	content.setSpacing(spacing);
    }

    public void setPadding(boolean padding) {
    	content.setPadding(padding);
    }
}
