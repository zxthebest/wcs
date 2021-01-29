package com.work.wcs.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PoiExcelClass {
	private static final String EXCEL_PATH = "c:\\constant.xlsx";

	/**
	 * 直接抽取excel中的数据
	 */
	public static void extract(String path) {
		InputStream inp = null;
		Workbook workbook = null;
		ExcelExtractor extractor = null;
		XSSFExcelExtractor xssfExtractor = null;
		String text = "";
		try {
			inp = new FileInputStream(path);
			workbook = WorkbookFactory.create(inp);
			if (workbook instanceof HSSFWorkbook) {
				extractor = new ExcelExtractor((HSSFWorkbook) workbook);
				extractor.setFormulasNotResults(true);
				extractor.setIncludeSheetNames(false);
				text = extractor.getText();
			} else if (workbook instanceof XSSFWorkbook) {
				xssfExtractor = new XSSFExcelExtractor((XSSFWorkbook) workbook);
				// 设置对于公式是否返回公式本身还是返回其计算结果，默认false(返回计算结果)
				xssfExtractor.setFormulasNotResults(true);
				// xssfExtractor.setIncludeSheetNames(false);
				text = xssfExtractor.getText();
			} else {
				return;
			}
			System.out.println(text);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (extractor != null) {
				try {
					extractor.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (xssfExtractor != null) {
				try {
					xssfExtractor.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inp != null) {
				try {
					inp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 原样返回数值单元格的内容
	 */
	public static String formatNumericCell(Double value, Cell cell) {
		if (cell.getCellTypeEnum() != CellType.NUMERIC && cell.getCellTypeEnum() != CellType.FORMULA) {
			return null;
		}
		// isCellDateFormatted判断该单元格是"时间格式"或者该"单元格的公式算出来的是时间格式"
		if (DateUtil.isCellDateFormatted(cell)) {
			// cell.getDateCellValue()碰到单元格是公式,会自动计算出Date结果
			// Date date = DateUtil.getJavaDate(value);
			Date date = cell.getDateCellValue();
			DataFormatter dataFormatter = new DataFormatter();
			Format format = dataFormatter.createFormat(cell);
			return format.format(date);
		} else {
			// String formatStr = cell.getCellStyle().getDataFormatString();
			// if (formatStr.contains("0;")) {
			// formatStr = "0";
			// }else if (formatStr.contains("0.000")) {
			// formatStr = "0.000";
			// } else if (formatStr.contains("0.00")) {
			// formatStr = "0.00";
			// } else if (formatStr.contains("0.0")) {
			// formatStr = "0.0";
			// } else if (formatStr.equals("General")) {
			// formatStr = "0";
			// } else if (formatStr.contains("GENERAL")) {
			// formatStr = "0";
			// } else if (formatStr.contains("0_")) {
			// formatStr = "0";
			// } else if (formatStr.equals("0")) {
			// formatStr = "0";
			// }
			// DecimalFormat df = new DecimalFormat(formatStr);
			// return df.format(value);
			DataFormatter dataFormatter = new DataFormatter();
			Format format = dataFormatter.createFormat(cell);
			return format.format(value);

		}
	}

	/*
	 * 通过对单元格遍历的形式来获取信息 ，这里要判断单元格的类型才可以取出值
	 */
	/**
	 * 
	 * <p>
	 * 读取excel内容并输出至控制台
	 * </p>
	 * 
	 * @param path
	 * @author 周欣(13667212859)
	 * @date 2017年2月9日 下午4:37:51
	 */
	public static void readWorkbook(String path) {
		InputStream inp = null;
		Workbook workbook = null;
		List<Map<String, Object>> valueList = new ArrayList<Map<String, Object>>();// 返回结果
		Map<Integer, String> keys = new HashMap<Integer, String>();

		try {
			inp = new FileInputStream(path);
			workbook = WorkbookFactory.create(inp);
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			// for/in是iterator的简写, 最终会被编译器编译为iterator
			// for(Iterator<Sheet> iterator=workbook.iterator();
			// iterator.hasNext();) {
			// Sheet sheet = iterator.next();
			for (Sheet sheet : workbook) {
				System.out.println("----------" + sheet.getSheetName() + "----------");
				int rows = sheet.getPhysicalNumberOfRows(); // 获取到Excel文件（一个sheet页）中的所有行数
				if (rows != 0) {
					Row firstRow = sheet.getRow(0);
					if (firstRow != null) {
						int cells = firstRow.getPhysicalNumberOfCells();// 获取列数
						System.out.println("cells=" + cells);
						// 遍历列,获取列名
						for (int j = 0; j < cells; j++) {
							// 获取到列的值­
							try {
								Cell cell = firstRow.getCell(j);
								if (null != cell) {
									String cellValue = cell.getStringCellValue();
									keys.put(j, cellValue);
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						// 展示列名
						for (Map.Entry<Integer, String> entry : keys.entrySet()) {
							System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
						}
						// 遍历行­（从第二行开始）
						for (int i = 1; i < rows; i++) {
							Row row = sheet.getRow(i);
							Map<String, Object> val = new HashMap<String, Object>();
							// 遍历列
							for (int j = 0; j < cells; j++) {

								Cell cell = row.getCell(j);
								switch (cell.getCellTypeEnum()) {
								case _NONE:
									System.out.print("_NONE" + "\t");
									val.put(keys.get(j), "_NONE");
									break;
								case BLANK:
									System.out.print("BLANK" + "\t");
									val.put(keys.get(j), "BLANK");
									break;
								case BOOLEAN:
									System.out.print(cell.getBooleanCellValue() + "\t");
									val.put(keys.get(j), cell.getBooleanCellValue());
									break;
								case ERROR:
									System.out.print("ERROR(" + cell.getErrorCellValue() + ")" + "\t");
									val.put(keys.get(j), "ERROR(" + cell.getErrorCellValue() + ")");
									break;
								case FORMULA:
									// 会打印出原本单元格的公式
									// System.out.print(cell.getCellFormula() +
									// "\t");
									// NumberFormat nf = new
									// DecimalFormat("#.#");
									// String value =
									// nf.format(cell.getNumericCellValue());
									CellValue cellValue = evaluator.evaluate(cell);
									switch (cellValue.getCellTypeEnum()) {
									case _NONE:
										System.out.print("_NONE" + "\t");
										val.put(keys.get(j), "_NONE");
										break;
									case BLANK:
										System.out.print("BLANK" + "\t");
										val.put(keys.get(j), "BLANK");
										break;
									case BOOLEAN:
										System.out.print(cellValue.getBooleanValue() + "\t");
										val.put(keys.get(j), cellValue.getBooleanValue());
										break;
									case ERROR:
										System.out.print("ERROR(" + cellValue.getErrorValue() + ")" + "\t");
										val.put(keys.get(j), "ERROR(" + cellValue.getErrorValue() + ")");
										break;
									case NUMERIC:
										System.out.print(formatNumericCell(cellValue.getNumberValue(), cell) + "\t");
										val.put(keys.get(j), formatNumericCell(cellValue.getNumberValue(), cell));
										break;
									case STRING:
										System.out.print(cell.getStringCellValue() + "\t");
										val.put(keys.get(j), cell.getStringCellValue());
										// System.out.print(cell.getRichStringCellValue()
										// + "\t");
										break;
									default:
										break;
									}
									break;
								case NUMERIC:
									String str = null;
									if (DateUtil.isCellDateFormatted(cell)) {
										str = new DataFormatter().formatRawCellContents(cell.getNumericCellValue(), 0,
												"yyyy-MM-dd HH:mm:ss");
									} else {
										str = String.valueOf(cell.getNumericCellValue());
									}
									System.out.print(str + "\t");
									val.put(keys.get(j), str);
									break;
								case STRING:
									System.out.print(cell.getStringCellValue() + "\t");
									val.put(keys.get(j), cell.getStringCellValue());
									// System.out.print(cell.getRichStringCellValue()
									// +
									// "\t");
									break;
								}

							}
							valueList.add(val);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inp != null) {
				try {
					inp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("*****************************");
		// System.out.println(valueList.size());
		for (String key : valueList.get(0).keySet()) {
			System.out.println(key + ":" + valueList.get(1).get(key));
		}

	}

	/**
	 * 
	 * <p>
	 * 获取excel内容，将结果存储至List<Map<String, Object>>，并返回
	 * </p>
	 * 
	 * @param path
	 *            path为excel文件的存储路径
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年2月9日 下午4:38:37
	 */
	public static List<Map<String, Object>> getWorkbook(String path) {
		InputStream inp = null;
		Workbook workbook = null;
		List<Map<String, Object>> valueList = new ArrayList<Map<String, Object>>();// 返回结果
		Map<Integer, String> keys = new HashMap<Integer, String>();

		try {
			inp = new FileInputStream(path);
			workbook = WorkbookFactory.create(inp);
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			// for/in是iterator的简写, 最终会被编译器编译为iterator
			// for(Iterator<Sheet> iterator=workbook.iterator();
			// iterator.hasNext();) {
			// Sheet sheet = iterator.next();
			for (Sheet sheet : workbook) {
				// System.out.println("----------" + sheet.getSheetName()
				// + "----------");
				int rows = sheet.getPhysicalNumberOfRows(); // 获取到Excel文件（一个sheet页）中的所有行数
				if (rows != 0) {
					Row firstRow = sheet.getRow(0);
					if (firstRow != null) {
						int cells = firstRow.getPhysicalNumberOfCells();// 获取列数
						int flag = 0;// 记录首行开始有值的列数（避免数据不是从第一列开始）
						// System.out.println("cells=" + cells);
						// 遍历列,获取列名
						for (int j = 0; j < cells; j++) {
							// 获取到列的值­
							try {
								Cell cell = firstRow.getCell(j);
								if (null != cell) {
									String cellValue = cell.getStringCellValue();
									// System.out.println("cellValue=" +
									// cellValue);
									keys.put(j, cellValue);
								} else {
									cells++;
									flag++;
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						// 展示列名
						// for (Map.Entry<Integer, String> entry :
						// keys.entrySet()) {
						// System.out.println("key= " + entry.getKey() + " and
						// value= " + entry.getValue());
						// }
						// 遍历行­（从第二行开始）
						for (int i = 1; i < rows; i++) {
							Row row = sheet.getRow(i);
							Map<String, Object> val = new HashMap<String, Object>();
							// 遍历列
							for (int j = flag; j < cells; j++) {

								Cell cell = row.getCell(j);
								if (null != cell) {
									switch (cell.getCellTypeEnum()) {
									case _NONE:
										// System.out.print("_NONE" + "\t");
										val.put(keys.get(j), "_NONE");
										break;
									case BLANK:
										// System.out.print("BLANK" + "\t");
										val.put(keys.get(j), "BLANK");
										break;
									case BOOLEAN:
										// System.out.print(cell.getBooleanCellValue()
										// + "\t");
										val.put(keys.get(j), cell.getBooleanCellValue());
										break;
									case ERROR:
										// System.out.print("ERROR("
										// + cell.getErrorCellValue() + ")"
										// + "\t");
										val.put(keys.get(j), "ERROR(" + cell.getErrorCellValue() + ")");
										break;
									case FORMULA:
										// 会打印出原本单元格的公式
										// System.out.print(cell.getCellFormula()
										// +
										// "\t");
										// NumberFormat nf = new
										// DecimalFormat("#.#");
										// String value =
										// nf.format(cell.getNumericCellValue());
										CellValue cellValue = evaluator.evaluate(cell);
										switch (cellValue.getCellTypeEnum()) {
										case _NONE:
											// System.out.print("_NONE" + "\t");
											val.put(keys.get(j), "_NONE");
											break;
										case BLANK:
											// System.out.print("BLANK" + "\t");
											val.put(keys.get(j), "BLANK");
											break;
										case BOOLEAN:
											// System.out.print(cellValue
											// .getBooleanValue() + "\t");
											val.put(keys.get(j), cellValue.getBooleanValue());
											break;
										case ERROR:
											// System.out.print("ERROR("
											// + cellValue.getErrorValue()
											// + ")" + "\t");
											val.put(keys.get(j), "ERROR(" + cellValue.getErrorValue() + ")");
											break;
										case NUMERIC:
											// System.out.print(formatNumericCell(
											// cellValue.getNumberValue(),
											// cell)
											// + "\t");
											val.put(keys.get(j), formatNumericCell(cellValue.getNumberValue(), cell));
											break;
										case STRING:
											// System.out.print(cell
											// .getStringCellValue() + "\t");
											val.put(keys.get(j), cell.getStringCellValue());
											// System.out.print(cell.getRichStringCellValue()
											// + "\t");
											break;
										default:
											break;
										}
										break;
									case NUMERIC:
										String str = null;
										if (DateUtil.isCellDateFormatted(cell)) {
											str = new DataFormatter().formatRawCellContents(cell.getNumericCellValue(),
													0, "yyyy-MM-dd HH:mm:ss");
										} else {
											str = String.valueOf(cell.getNumericCellValue());
										}
										// System.out.print(str + "\t");
										val.put(keys.get(j), str);
										break;
									case STRING:
										// System.out.print(cell.getStringCellValue()
										// + "\t");
										val.put(keys.get(j), cell.getStringCellValue());
										// System.out.print(cell.getRichStringCellValue()
										// +
										// "\t");
										break;
									}

								}
							}
							valueList.add(val);

						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inp != null) {
				try {
					inp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// System.out.println("*****************************");
		// System.out.println(valueList.size());
		// for (String key : valueList.get(0).keySet()) {
		// System.out.println(key + ":" + valueList.get(1).get(key));
		// }
		return valueList;
	}

	public static void main(String[] args) {
		// extract(EXCEL_PATH);
		// readWorkbook(EXCEL_PATH);
		List<Map<String, Object>> list = getWorkbook(EXCEL_PATH);
		// System.out.println("*****************************");
		System.out.println(list);
	}
}
