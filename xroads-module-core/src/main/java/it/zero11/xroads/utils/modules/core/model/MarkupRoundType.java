package it.zero11.xroads.utils.modules.core.model;

public enum MarkupRoundType {
	None, // ("No rounding (es. 23.65)"), 		//12.34 		22.87
	Round000, //("Round to xxx.00 (es. 24.00)"), 	//12.00		23.00
	Round099, //("Round to xxx.99 (es. 23.99)"), 	//11.99		22.99
	Round900, //("Round to xx9.00 (es. 29.00)"), 	//9.00		19.00
	Round999, //("Round to xx9.99 (es. 29.99)"), 	//9.99		19.99
	Round90000, //("Round to xx900 (es. 2900)"), 	//9.00		19.00
	Round99000, //("Round to xx990 (es. 2990)"), 	//9.99		19.99
	Round00000, //("Round to x000 (es. 2000)"), 	//9.00		19.00
	Round900000, //("Round to xx9000 (es. 29000)"), 	//9.00		19.00
	Round990000, //("Round to xx9900 (es. 29900)"), 	//9.99		19.99
	Round000000, //("Round to xx0000 (es. 20000)"); 	//9.00		19.00

}
