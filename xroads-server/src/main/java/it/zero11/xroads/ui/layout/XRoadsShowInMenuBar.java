package it.zero11.xroads.ui.layout;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.vaadin.flow.component.icon.VaadinIcon;

@Retention(RUNTIME)
@Target(TYPE)
public @interface XRoadsShowInMenuBar {
	String name();
	boolean i18n() default false;
	VaadinIcon[] icon() default {};
	String iconURL() default "";
	int order();
}
