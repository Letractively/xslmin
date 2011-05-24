package com.googlecode.monkeybrown.xslmin;

import javax.xml.xpath.XPathConstants;

import junit.framework.TestCase;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XslLocalParamRenamerTest extends TestCase
{
	private static final String LOCAL_PARAM_XPATH = "//xsl:template/descendant::xsl:param";
	
	private boolean hasRun = false;
	public void setUp()
	{
		if(!hasRun)
		{
			hasRun = true;
			XslMinTestUtils.runXslMin();
		}
	}
	
	/**
	 * Test that the local params still exist
	 */
	public void testAllStillExist() 
	{
		String count = String.format("count(%s)", LOCAL_PARAM_XPATH);
		double countBefore = (Double) XslMinTestUtils.executeXpathOnUnminifiedResult(count, XPathConstants.NUMBER);
		double countAfter = (Double) XslMinTestUtils.executeXpathOnMinifiedResult(count, XPathConstants.NUMBER);
		assertEquals(true, (countBefore > 0) && (countAfter == countBefore));
	}
	
	/**
	 * Test that the local params have the same value they did before minification
	 */
	public void testValuesUntouched() 
	{
		Node firstLocalParamSelect = (Node)XslMinTestUtils.executeXpathOnMinifiedResult(LOCAL_PARAM_XPATH + "/@select", XPathConstants.NODE);
		assertEquals("'localA'", firstLocalParamSelect.getNodeValue());
	}
	
	/**
	 * Test that the local params were actually renamed
	 */
	public void testDefinitionsWereRenamed() 
	{
		NodeList params = XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:template[not(@match)]/descendant::xsl:param/@name");
		for(int i=0; i<params.getLength(); i++)
		{
			assertEquals(1, params.item(i).getNodeValue().length());
		}
	}
	
	/**
	 * Test that local params in templates with a match attribute were NOT renamed
	 */
	public void testExcludedDefinitionsNotRenamed() 
	{
		String xpath = "//xsl:template[@match]/descendant::xsl:param/@name";
		NodeList beforeMin = XslMinTestUtils.executeXpathOnUnminifiedResult(xpath);
		NodeList afterMin = XslMinTestUtils.executeXpathOnMinifiedResult(xpath);
		for(int i=0; i< Math.min(beforeMin.getLength(), afterMin.getLength()); i++)
		{
			assertTrue(beforeMin.item(i).getNodeValue().equals(afterMin.item(i).getNodeValue()));
		}
	}

	/**
	 * Test that local variables and local params are not renamed to the same name in the same scope
	 * PRECONDITION: both the local variable renaming AND the local parameter renaming must have been executed for this test to prove anything
	 */
	public void testNoClash() 
	{
		Node var1 = (Node) XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:template/descendant::xsl:variable/@name", XPathConstants.NODE);//first var
		Node param1 = (Node) XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:template/descendant::xsl:param/@name", XPathConstants.NODE);//first param
		assertFalse(var1.getNodeValue().equals(param1.getNodeValue()));
	}
}
