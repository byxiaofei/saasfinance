package com.sinosoft.util;

import org.apache.poi.ss.formula.functions.T;

import java.io.*;
import java.text.MessageFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 封装了XML转换成object，object转换成XML的
 * 
 * @author Luodj
 *
 */
public class XMLUtil {
	/**
	 * 将对象直接转换成String类型的 XML输出
	 * 
	 * @param obj
	 * @return
	 */
	public static String convertToXml(Object obj) {
		// 创建输出流
		StringWriter sw = new StringWriter();
		try {
			// 利用jdk中自带的转换类实现
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			// 格式化xml输出的格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			// 将对象转换成输出流形式的xml
			marshaller.marshal(obj, sw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	/**
	 * 将对象根据路径转换成xml文件
	 * 
	 * @param obj
	 * @param path
	 * @return
	 */
	public static void convertToXml(Object obj, String path) {
		try {
			// 利用jdk中自带的转换类实现
			JAXBContext context = JAXBContext.newInstance(obj.getClass());

			Marshaller marshaller = context.createMarshaller();
			// 格式化xml输出的格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			// 将对象转换成输出流形式的xml
			// 创建输出流
			FileWriter fw = null;
			try {
				fw = new FileWriter(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			marshaller.marshal(obj, fw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将String类型的xml转换成对象
	 */
	@SuppressWarnings("unchecked")
	public static Object convertXmlStrToObject(Class clazz, String xmlStr) {
		Object xmlObject = null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			// 进行将Xml转成对象的核心接口
			Unmarshaller unmarshaller = context.createUnmarshaller();
			StringReader sr = new StringReader(xmlStr);
			xmlObject = unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return xmlObject;
	}

	@SuppressWarnings("unchecked")
	/** 
	 * 将file类型的xml转换成对象 
	 */
	public static Object convertXmlFileToObject(Class clazz, String xmlPath) {
		Object xmlObject = null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			FileReader fr = null;
			try {
				fr = new FileReader(xmlPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			xmlObject = unmarshaller.unmarshal(fr);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return xmlObject;
	}

	/**
	 *
	 * 功能描述:		流形式解析成JavaBean
	 *
	 */
	public static <T> T converToJavaBean(Class<T> clz,InputStream file){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(clz);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			T unmarshal = (T)unmarshaller.unmarshal(file);
			return unmarshal;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void convertToXmlFile(Object obj,String savePath) {
		File file = null;
		String xmlStr = convertToXml(obj);
		if(xmlStr != null && !"".equals(xmlStr)) {

			file = new File(savePath);
			if(!file.exists() && file.isFile()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(file.isDirectory()) {
				System.out.println(MessageFormat.format("{0}不是有效的文件路径.", savePath));
			}

			Writer writer = null;

			try {
				writer = new FileWriter(file);
				writer.write(xmlStr);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
