const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<custom-style> 
  <style include="view-styles">
		.xroads-item{
		    display: inline-block;
		    padding: 0px 8px;
		    margin-right: 4px;
		    background: var(--lumo-primary-color);
		    color: var(--lumo-primary-contrast-color);
		    border-radius: 30px;
		    user-select: none;
		    pointer-events:none;
		}
		.xroads-wrapper{
			pointer-events:none;
		}
		
		:root {
			--app-drawer-width: 200px;
		}
		body{
			background-color: #F9FAFB;
		}
		
		html{
			--lumo-body-text-color: #0f0f0f;
		
			--lumo-border-radius: 0.1em;
			--lumo-font-family: 'Inter', 'Montserrat', 'Arial', 'Helvetica', 'sans-serif' !important;
			font-family: var(--lumo-font-family) !important;
			--lumo-shade: #0f0f0f;
	        --lumo-disable-text-color: hsl(0, 0%, 75%) !important;
		}
		
		h1, h2, h3, h4, h5, h6 {
			color: var(--lumo-header-text-color);
		}
	
		h1 {
			font-size: var(--lumo-font-size-xxxl);
			margin-bottom: 0.75em;
			text-transform: uppercase;
		}
		
		p.subtitle{
			font-weight: 500;
			margin-bottom: var(--lumo-space-m);
			font-size: var(--lumo-font-size-l);
			margin: 0 auto;
		}
		
		p.subtitle-big{
			font-size: var(--lumo-font-size-xl);
			font-weight: 500;
			margin: 10px auto 2px auto;
		}
		
		p.help-description{
			font-size: var(--lumo-font-size-s);
			margin-top: var(--lumo-space-m);
    		padding-left:  var(--lumo-space-l);
			
		}	
		
		.price{
			font-size: var(--lumo-font-size-xl);
		    font-weight: 600;
		    margin:  var(--lumo-space-m) auto var(--lumo-space-s) auto; 
		    color: var(--lumo-shade-80pct);
		}
		.price span{
			font-weight: 500;
			color: var(--lumo-shade);
		}
		
		.activate-description{
			font-size: var(--lumo-font-size-xs);
   			margin: var(--lumo-space-m) auto;
		}
		
		.app-menu-item:hover, .app-menu-item[highlight]{
			text-decoration: none;
		}
		
		.vaadin-button-container {
			text-transform: uppercase;
			letter-spacing: 1px;
			--lumo-border-radius-l: 2em;
			border-radius: 2em;
		}
		
    </style> 
 </custom-style><dom-module id="zero11-form-layout-style" theme-for="vaadin-form-layout"> 
  <template> 
   <style>
    	#layout {
		    align-items: flex-start;
		}
    </style> 
  </template> 
 </dom-module><dom-module id="zero11-button-styles" theme-for="vaadin-button"> 
  <template> 
   <style>
       
	:host { 
		    background-color:transparent;
		    color: var(--lumo-primary-color);
		    --lumo-button-size: var(--lumo-size-l);
		    border: solid 2px;
       		border-color: var(--lumo-primary-color);
	     	width: fit-content;
		    max-width: 500px;
		    white-space: normal;
		    height: auto;
		    --lumo-border-radius-l: 2em;
		    border-radius: var(--lumo-border-radius-l) !important;
		}
		
	[part="overlay"] {
		border-radius: var(—lumo-border-radius-s);
		background-color: var(--lumo-tint-90pct) !important;
	}
	
	[part="label"] {
	    white-space: normal;
	    overflow: hidden;
	    text-overflow: ellipsis;
	}
		
	:host([theme~="abort"]){
			background-color: transparent;
			color: var(--lumo-shade-70pct);
			border:none;
		}
		
	 :host([theme~="abort"]:hover) {
	 	  background-color: transparent;
          color: var(--lumo-shade-60pct);
        }
        
      :host([theme~="error"][focused]){
			background-color: transparent;
			color: var(--lumo-shade-70pct);
		}

	:host([focused]) {
	        background-color: var(--lumo-primary-color-10pct);
	        color: var(--lumo-primary-color);
      	}
    
   	:host(:hover) {
			background-color: var(--lumo-primary-color-5pct) !important;
			color: var(--lumo-primary-color) !important;
   		}
   		
	
	 :host([theme~="complete"]){
		background-color: var(--lumo-primary-color);
		color: var(--lumo-tint);
	}
	
	 :host([theme~="complete"]:hover){
		background-color: var(--lumo-primary-color-90pct);
		color: var(--lumo-tint);
	}
	
	:host([theme~="tertiary-inline"]), :host([theme~="link"]){
		color:var(--lumo-primary-color);
		cursor: pointer;
		border: none;
	}
	:host([theme~="secondary-inline"]), :host([theme~="link"]){
		color:var(--lumo-primary-color);
		cursor: pointer;
		border: none;
		font-size: var(--lumo-font-size-xs);
	}
	
    </style> 
  </template> 
 </dom-module><dom-module id="zero11-checkbox" theme-for="vaadin-checkbox"> 
  <template> 
   <style>
      :host{
      	font-weight: 500;
      }
    </style> 
  </template> 
 </dom-module><dom-module id="zero11-form-layout" theme-for="vaadin-form-layout"> 
  <template> 
   <style>
      :host{
      	opacity: 0.99 !important;
      }
    </style> 
  </template> 
 </dom-module><dom-module id="zero11-text-fields" theme-for="vaadin-text-field vaadin-text-area vaadin-checkbox vaadin-select vaadin-combo-box"> 
  <template> 
   <style>
      :host([has-label]) {
        padding-top:0px;
      }

    </style> 
  </template> 
 </dom-module>`;

document.head.appendChild($_documentContainer.content);

/* WORKAROUND FOR SAFARI BUG https://github.com/vaadin/vaadin-form-layout/issues/111 */
/*
  FIXME(polymer-modulizer): the above comments were extracted
  from HTML and may be out of place here. Review them and
  then delete this comment!
*/
;