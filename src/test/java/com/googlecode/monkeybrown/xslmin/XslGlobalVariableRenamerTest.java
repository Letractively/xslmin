package com.googlecode.monkeybrown.xslmin;

import javax.xml.xpath.XPathConstants;

import junit.framework.TestCase;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XslGlobalVariableRenamerTest extends TestCase
{
	private static final String GLOBAL_VAR_XPATH = "//xsl:stylesheet/xsl:variable";
	private boolean hasRun = false;

	public void setUp()
	{
		if(!hasRun)
		{
			hasRun = true;
			XslMinTestUtils.runXslMin();
		}
	}

	private static NodeList getGlobalVariables()
	{
		return XslMinTestUtils.executeXpathOnMinifiedResult(GLOBAL_VAR_XPATH);
	}

	/**
	 * Test that the global variables still exist
	 */
	public void testAllStillExist()
	{
		String count = String.format("count(%s)", GLOBAL_VAR_XPATH);
		double countBefore = (Double) XslMinTestUtils.executeXpathOnUnminifiedResult(count, XPathConstants.NUMBER);
		double countAfter = (Double) XslMinTestUtils.executeXpathOnMinifiedResult(count, XPathConstants.NUMBER);
		assertEquals(true, (countBefore > 0) && (countAfter == countBefore));
	}

	/**
	 * Test that the global variables have the same value theygetGlobalVariables did before minification
	 */
	public void testValuesUntouched()
	{
		NodeList vars = getGlobalVariables();
		assertEquals("barfoo", vars.item(0).getFirstChild().getAttributes().getNamedItem("select").getNodeValue());
	}

	/**
	 * Test that the global variables were actually renamed
	 */
	public void testDefinitionsWereRenamed()
	{
		NodeList vars = getGlobalVariables();
		for(int i=0; i<vars.getLength(); i++)
		{
			assertEquals(true, vars.item(i).getAttributes().getNamedItem("name").getNodeValue().length() == 1);
		}
	}

	/**
	 * Test that the references to global variables were correctly renamed
	 */
	public void testReferencesWereRenamed()
	{
		Node globalDef = (Node) XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:variable[@handle='def1']/@name", XPathConstants.NODE);
		Node globalRef = (Node) XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:value-of[@handle='ref1']/@select", XPathConstants.NODE);
		String varName = globalDef.getNodeValue();
		String variableUse = globalRef.getNodeValue();
		assertEquals(true, variableUse.endsWith("$" + varName + ")"));
	}

	/**
	 * Test that the references to global variables were correctly renamed
	 */
	public void testReferencesWereRenamed2()
	{
		Node globalDef = (Node) XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:variable[@handle='gvStrippedDef']/@name", XPathConstants.NODE);
		Node globalRef = (Node) XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:value-of[@handle='gvStrippedRef']/@select", XPathConstants.NODE);
		String varName = globalDef.getNodeValue();
		String variableUse = globalRef.getNodeValue();
		assertEquals(true, variableUse.contains("normalize-space($" + varName + ")"));
	}

	/**
	 * Test that references to local variables which have the smae name as global variables were not renamed
	 * Note, this test could fail if the variables accidentally but legitimately get the same name. The test xsl file
	 * should be crafted so this doesn't happen.
	 */
	public void testShadowsWereNotRenamed()
	{
		Node globalDef = (Node) XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:variable[@handle='def1']/@name", XPathConstants.NODE);
		Node globalRef = (Node) XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:value-of[@handle='noref1']/@select", XPathConstants.NODE);
		String varName = globalDef.getNodeValue();
		String variableUse = globalRef.getNodeValue();
		assertEquals(false, variableUse.endsWith("$" + varName + ")"));
	}

	/**
	 * Test that global variables and global params are not renamed to the same name in the same scope
	 * PRECONDITION: both the variable renaming AND the parameter renaming must have been executed for this test to prove anything
	 */
	public void testNoClash()
	{
		NodeList varNames = XslMinTestUtils.executeXpathOnMinifiedResult(GLOBAL_VAR_XPATH + "/@name");
		NodeList paramNames = XslMinTestUtils.executeXpathOnMinifiedResult("//xsl:stylesheet/xsl:param/@name");
		for(int i=0; i< Math.min(varNames.getLength(), paramNames.getLength()); i++)
		{
			assertFalse(varNames.item(i).getNodeValue().equals(paramNames.item(i).getNodeValue()));
		}
	}

}
