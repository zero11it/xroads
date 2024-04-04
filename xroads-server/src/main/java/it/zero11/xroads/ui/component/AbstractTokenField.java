package it.zero11.xroads.ui.component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.internal.JavaScriptSemantics;

public abstract class AbstractTokenField<T, C extends AbstractField<C, T>, K extends CustomField<T[]>> extends CustomField<T[]> {
	private static final long serialVersionUID = 1L;

	protected final FlexComponent layout;
	protected final C addTokenComboBox;
	protected final Button addTokenButton;

	private final List<T> values;
	
	private final boolean sortable;

	protected abstract C buildAddTokenComboBox();
	protected abstract Class<T> getTokenClass();
	
	public AbstractTokenField(String addButtonLabel){
		this(false, false, addButtonLabel);
	}

	public AbstractTokenField(boolean verticalLayout, String addButtonLabel){
		this(verticalLayout, false, addButtonLabel);
	}

	@SuppressWarnings("unchecked")
	public AbstractTokenField(boolean verticalLayout, boolean sortable, String addButtonLabel){
		this.sortable = sortable;
		this.values = new ArrayList<>();
		
		if (verticalLayout){
			layout = new VerticalLayout();
			((VerticalLayout)layout).setPadding(false);
			((VerticalLayout)layout).setSpacing(true);
		}else{
			layout = new FlexLayout();
			((FlexLayout)layout).setFlexWrap(FlexWrap.WRAP);
			layout.setWidth("100%");
		}

		addTokenButton = new Button(addButtonLabel, VaadinIcon.PLUS.create());
		addTokenComboBox = buildAddTokenComboBox();
		
		addTokenComboBox.addValueChangeListener((event)-> {
			if (event.isFromClient() && event.getValue() != null){
				try{
					addValue(event.getValue());
				}catch (Exception e) {
				}

				addTokenButton.setVisible(true);
				addTokenComboBox.setVisible(false);
			}
		});
		
		if (addTokenComboBox instanceof ComboBox) {
			addTokenComboBox.getElement().addPropertyChangeListener("opened", (event)->{
				if (event.getValue() != null) {
					if (!JavaScriptSemantics.isTrueish(event.getValue())){
						addTokenButton.setVisible(true);
						addTokenComboBox.setVisible(false);
					}
				}
			});
		}else {
			((Focusable<C>) addTokenComboBox).addBlurListener((event) -> {
				addTokenButton.setVisible(true);
				addTokenComboBox.setVisible(false);
			});
		}
		addTokenButton.addClickListener((event)->{
			addTokenButton.setVisible(false);
			addTokenComboBox.setVisible(true);
			addTokenComboBox.setValue(null);
			if (addTokenComboBox instanceof ComboBox) {
				((ComboBox<?>) addTokenComboBox).setOpened(true);
			}
			((Focusable<C>)addTokenComboBox).focus();
		});
		refresh();
		
		if (sortable){
			throw new UnsupportedOperationException();
		}else{
			add((Component)layout);
		}
	}

	public void setAddButtonCaption(String caption){
		addTokenButton.setText(caption);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected T[] generateModelValue() {
		T[] data = (T[]) Array.newInstance(getTokenClass(), this.values.size());
		return this.values.toArray(data);
	}

	@Override
	protected void setPresentationValue(T[] value) {
		this.values.clear();
		this.values.addAll(Arrays.asList(value));
		refresh();
	}

	private void refresh() {
		layout.removeAll();
		if (values != null){
			for (final T value:values){
				Button valueButton = new Button(getItemCaption(value), VaadinIcon.CLOSE_CIRCLE.create(), (event) -> {
					removeValue(value);
				});
				valueButton.getElement().getStyle().set("margin-right", "var(--lumo-space-xs)");
				if (!sortable){
					layout.add(valueButton);
				}
			}
		}
		addTokenComboBox.setVisible(false);
		addTokenButton.setVisible(true);
		layout.add((Component)addTokenComboBox);
		layout.add((Component)addTokenButton);
	}

	protected abstract String getItemCaption(T value);
	protected abstract Object getComboBoxItemCaptionPropertyId();
	protected abstract boolean supportNewValue();

	public void addValue(T value){
		if (!values.contains(value)){
			values.add(value);
			updateValue();
			refresh();
		}
	}

	public void removeValue(T value){
		if (values.remove(value)) {
			updateValue();
			refresh();
		}
	}
}