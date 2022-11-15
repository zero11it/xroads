package it.zero11.xroads.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class POIUtils {
	private final static Set<String> TRUE_VALUES;

	static {
		Set<String> trueValues = new HashSet<String>();
		trueValues.add("yes");
		trueValues.add("sì");
		trueValues.add("si");
		trueValues.add("true");
		trueValues.add("1");

		TRUE_VALUES = Collections.unmodifiableSet(trueValues);
	}

	private static DataFormatter formatter = new DataFormatter();
	public static String getString(Cell cell) {
		switch (cell.getCellType()) {
		case FORMULA:
			switch (cell.getCachedFormulaResultType()) {
			case NUMERIC:
				final CellStyle cellStyle = cell.getCellStyle();
				return formatter.formatRawCellContents(cell.getNumericCellValue(), cellStyle.getDataFormat(), cellStyle.getDataFormatString());
			case STRING:
				return cell.getStringCellValue();
			case BLANK:
				return "";
			default:
				throw new IllegalArgumentException("Invalid cell type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
			}
		default:
			return formatter.formatCellValue(cell);
		}
	}
	
	/**
	 * If cell is numeric it will return the number without any excel formatting
	 */
	public static String getLongOrString(Cell cell) {
		switch (cell.getCellType()) {
		case FORMULA:
			switch (cell.getCachedFormulaResultType()) {
			case NUMERIC:
				return Long.toString((long)cell.getNumericCellValue());
			case STRING:
				return cell.getStringCellValue();
			case BLANK:
				return "";
			default:
				throw new IllegalArgumentException("Invalid cell type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
			}
		case NUMERIC:
			return Long.toString((long) cell.getNumericCellValue());
		default:
			return formatter.formatCellValue(cell);
		}
	}
	
	public static Integer getInteger(Cell cell) {
		try {
			switch (cell.getCellType()) {
			case NUMERIC:
				return (int) cell.getNumericCellValue();
			case STRING:
			{
				String value = cell.getStringCellValue().trim();
				if (value.length() == 0) {
					return null;
				}else {
					return Integer.valueOf(value);
				}
			}
			case BLANK:
				return null;
			case FORMULA:
				switch (cell.getCachedFormulaResultType()) {
				case NUMERIC:
					return (int) cell.getNumericCellValue();
				case STRING:
				{
					String value = cell.getStringCellValue().trim();
					if (value.length() == 0) {
						return null;
					}else {
						return Integer.valueOf(value);
					}
				}
				case BLANK:
					return null;
				default:
					throw new IllegalArgumentException("Invalid cell type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
				}
			default:
				throw new IllegalArgumentException("Invalid cell type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
			}
		}catch(NumberFormatException e) {
			throw new IllegalArgumentException("Invalid number format type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
		}
	}

	public static BigDecimal getBigDecimal2Digits(Cell cell) {
		try {
			switch (cell.getCellType()) {
			case NUMERIC:
				return new BigDecimal(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
			case STRING:
			{
				String value = cell.getStringCellValue().trim();
				if (value.length() == 0) {
					return null;
				}else {
					return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
				}
			}
			case BLANK:
				return null;
			case FORMULA:
				switch (cell.getCachedFormulaResultType()) {
				case NUMERIC:
					return new BigDecimal(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
				case STRING:
				{
					String value = cell.getStringCellValue().trim();
					if (value.length() == 0) {
						return null;
					}else {
						return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
					}
				}
				case BLANK:
					return null;
				default:
					throw new IllegalArgumentException("Invalid cell type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
				}
			default:
				throw new IllegalArgumentException("Invalid cell type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
			}
		}catch(NumberFormatException e) {
			throw new IllegalArgumentException("Invalid number format type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
		}
	}

	public static boolean getBoolean(Cell cell) {
		switch (cell.getCellType()) {
		case NUMERIC:
			return cell.getNumericCellValue() == 1;
		case STRING:
			return TRUE_VALUES.contains(cell.getStringCellValue().trim().toLowerCase());
		case BLANK:
			return false;
		case FORMULA:
			switch (cell.getCachedFormulaResultType()) {
			case NUMERIC:
				return cell.getNumericCellValue() == 1;
			case STRING:
				return TRUE_VALUES.contains(cell.getStringCellValue().trim().toLowerCase());
			case BLANK:
				return false;
			default:
				throw new IllegalArgumentException("Invalid cell type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
			}
		default:
			throw new IllegalArgumentException("Invalid cell type Row:" + (cell.getRowIndex() + 1) + " Column: " + (cell.getColumnIndex() + 1));
		}
	}

	public static Workbook create(InputStream source) throws EncryptedDocumentException, InvalidFormatException, IOException {
		Workbook workbook = WorkbookFactory.create(source);
		workbook.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
		return workbook;
	}

	public static Workbook create(File source) throws EncryptedDocumentException, InvalidFormatException, IOException {
		Workbook workbook = WorkbookFactory.create(source);
		workbook.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
		return workbook;
	}

}
