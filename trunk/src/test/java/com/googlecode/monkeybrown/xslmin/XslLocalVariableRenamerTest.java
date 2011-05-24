package com.googlecode.monkeybrown.xslmin;

import javax.xml.xpath.XPathConstants;

import junit.framework.TestCase;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XslLocalVariableRenamerTest extends TestCase
{
	private static final String LOCAL_VAR_XPATH = "//xsl:template/descendant::xsl:variable";
	
	private boolean hasRun = false;
	public void setUp()
	{
		if(!hasRun)
		{
			hasRun = true;
			XslMinTestUtils.runXslMin();
		}
	}
	
	private static NodeList getLocalVariables()
	{
		return XslMinTestUtils.executeXpathOnMinifiedResult(LOCAL_VAR_XPATH);
	}
	
	/**
	 * Test that the local variables still exist
	 */
	public void testAllStillExist() 
	{
		String count = String.format("count(%s)", LOCAL_VAR_XPATH);
		double countBefore = (Double) XslMinTestUtils.executeXpathOnUnminifiedResult(count, XPathConstants.NUMBER);
		double countAfter = (Double) XslMinTestUtils.executeXpathOnMinifiedResult(count, XPathConstants.NUMBER);
		assertEquals(true, (countBefore > 0) && (countAfter == countBefore));
	}
	
	/**
	 * Test that the local variables have the same value they did before minification
	 */
	public void testValuesUntouched() 
	{
		NodeList vars = getLocalVariables();
		assertEquals("localB", vars.item(0).getFirstChild().getTextContent());
	}
	
	/**
	 * Test that the local variables were actually renamed
	 */
	public void testDefinitionsWereRenamed() 
	{
		NodeList vars = getLocalVariables();
		for(int i=0; i<vars.getLength(); i++)
		{
			assertEquals(true, vars.item(i).getAttributes().getNamedItem("name").getNodeValue().length() == 1);
		}
	}
	
	/**
	 * Test that the references to local variables were correctly renamed
	 */
	public void testReferencesWereRenamed() 
	{
		String xpath = "//xsl:template[@handle='tmpl1']/xsl:variable[position()=%d]";
		
		Node ref1 = (Node) XslMinTestUtils.executeXpathOnMinifiedResult(String.format(xpath, 1), XPathConstants.NODE);
		Node ref2 = (Node) XslMinTestUtils.executeXpathOnMinifiedResult(String.format(xpath, 2), XPathConstants.NODE);
		Node ref3 = (Node) XslMinTestUtils.executeXpathOnMinifiedResult(String.format(xpath, 3), XPathConstants.NODE);
		String ref1Name = ref1.getAttributes().getNamedItem("name").getNodeValue();
		String ref2Name = ref2.getAttributes().getNamedItem("name").getNodeValue();
		String ref3Value = ref3.getAttributes().getNamedItem("select").getNodeValue();
		
		assertEquals(String.format("concat($%s,$%s)", ref1Name, ref2Name), ref3Value);
	}
	
	/**
	 * Test that the references to local variables were correctly renamed
	 */
	public void testPartialMatchesNotRenamed() 
	{
		String varNameXp = "//xsl:template/descendant::xsl:variable[@handle='partialMatchA']/@name";
		String valOfSelectXp = "//xsl:template/descendant::xsl:value-of[@handle='partialMatchA']/@select";
		//First run the test on the UN-minified XSL to check this test is testing something
		Node var = (Node) XslMinTestUtils.executeXpathOnUnminifiedResult(varNameXp, XPathConstants.NODE);
		Node valOf = (Node) XslMinTestUtils.executeXpathOnUnminifiedResult(valOfSelectXp, XPathConstants.NODE);
		assertTrue(valOf.getNodeValue().indexOf(var.getNodeValue()) > -1);//check that this unit test is working
		var = (Node) XslMinTestUtils.executeXpathOnMinifiedResult(varNameXp, XPathConstants.NODE);
		valOf = (Node) XslMinTestUtils.executeXpathOnMinifiedResult(valOfSelectXp, XPathConstants.NODE);
		assertEquals(-1, valOf.getNodeValue().indexOf(var.getNodeValue()));//check that the minifier didn't do evil
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
