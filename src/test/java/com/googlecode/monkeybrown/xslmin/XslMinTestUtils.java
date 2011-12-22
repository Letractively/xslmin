package com.googlecode.monkeybrown.xslmin;


import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class XslMinTestUtils
{
	public static boolean preserve = true;//todo rejig all the tests to work with node removal (sigh...)
	public static final byte UNUSED_TEMPLATES = 1;
	public static final String TEST_XSL = "target/test-classes/test.xsl";
	public static final String TEST_OUT = "test.min.xsl";

	public static void runXslMin()
	{
		if(XslMinTestUtils.preserve)
		{
			String [] args = {TEST_XSL, TEST_OUT, "-p"};
			XslMin.main(args);
		}
		else
		{
			String [] args = {TEST_XSL, TEST_OUT};
			XslMin.main(args);
		}
	}

	public static Node getResultXsl()
	{
		return XpathUtils.loadXmlDoc(TEST_OUT).getDocumentElement();
	}

	public static Node getSourceXsl()
	{
		return XpathUtils.loadXmlDoc(TEST_XSL).getDocumentElement();
	}

	private static Object executeXpathOnDoc(Node root, String xpath, QName returnType)
	{
		Object result;
		try
		{
			result = XpathUtils.executeQuery(root, xpath, returnType);
		}
		catch (XPathExpressionException e)
		{
			result = null;
			e.printStackTrace();
		}
		return result;
	}
}
