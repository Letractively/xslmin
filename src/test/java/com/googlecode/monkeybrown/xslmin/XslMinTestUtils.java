package com.googlecode.monkeybrown.xslmin;


import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XslMinTestUtils
{	
	public static final String TEST_XSL = "target/test-classes/test.xsl";
	public static final String TEST_OUT = "test.min.xsl";
	private static Document unminifiedDoc;
	private static Document minifiedDoc;
		
	public static void runXslMin()
	{
		unminifiedDoc = null;//cached copy is invalid now
		minifiedDoc = null;//cached copy is invalid now
		String [] args = {TEST_XSL, TEST_OUT};
		XslMin.main(args);
	}
	
	private static Document getResultXsl()
	{
		if(minifiedDoc == null)
		{
			minifiedDoc = XslMin.xpathUtils.loadXmlDoc(TEST_OUT);
		}
		return minifiedDoc;
	}
	
	private static Document getSourceXsl()
	{
		if(unminifiedDoc == null)
		{
			unminifiedDoc = XslMin.xpathUtils.loadXmlDoc(TEST_XSL);
		}
		return unminifiedDoc;
	}
	
	private static Object executeXpathOnDoc(Document doc, String xpath, QName returnType)
	{
		Object result;
		try
		{
			result = XslMin.xpathUtils.executeQuery(doc, xpath, returnType);
		}
		catch (XPathExpressionException e)
		{
			result = null;
			e.printStackTrace();
		}
		return result;
	}
	
	public static NodeList executeXpathOnMinifiedResult(String xpath)
	{
		return (NodeList) executeXpathOnMinifiedResult(xpath, XPathConstants.NODESET);
	}
	
	public static Object executeXpathOnMinifiedResult(String xpath, QName returnType)
	{
		return executeXpathOnDoc(getResultXsl(), xpath, returnType);
	}
	
	public static NodeList executeXpathOnUnminifiedResult(String xpath)
	{
		return (NodeList) executeXpathOnMinifiedResult(xpath, XPathConstants.NODESET);
	}
	
	public static Object executeXpathOnUnminifiedResult(String xpath, QName returnType)
	{
		return executeXpathOnDoc(getSourceXsl(), xpath, returnType);
	}
}
