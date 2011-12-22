package com.googlecode.monkeybrown.xslmin;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

import javax.xml.xpath.XPathConstants;

import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 * @author prrbcl
 */
public class XslNamedTemplateRenamerTest extends TestCase
{
	private boolean hasRun = false;
	private static final String ALL_TEMPLATE_XPATH = "//xsl:template";

	public void setUp()
	{
		if(!hasRun)
		{
			hasRun = true;
			XslMinTestUtils.runXslMin();
		}
	}

	/**
	 * Test that the templates still exist
	 */
	public void testAllStillExist()
	{
		String count = String.format("count(%s)", ALL_TEMPLATE_XPATH);
		try
		{
			double countBefore = (Double) XpathUtils.executeQuery(XslMinTestUtils.getSourceXsl(), count, XPathConstants.NUMBER);
			double countAfter = (Double) XpathUtils.executeQuery(XslMinTestUtils.getResultXsl(), count, XPathConstants.NUMBER);
			double expected = countBefore;
			if(!XslMinTestUtils.preserve)
			{
				expected -= XslMinTestUtils.UNUSED_TEMPLATES;
			}
			assertEquals(true, (countBefore > 0) && (countAfter == expected));
		}
		catch(XPathExpressionException ex)
		{
			fail(ex.getMessage());
		}
	}

	public void testTemplates()
	{
		try
		{
			NodeList beforeTemplates = XpathUtils.executeQuery(XslMinTestUtils.getSourceXsl(), ALL_TEMPLATE_XPATH);
			NodeList afterTemplates = XpathUtils.executeQuery(XslMinTestUtils.getResultXsl(), ALL_TEMPLATE_XPATH);
			assertTrue("Nothing to test!", beforeTemplates.getLength() > 0);
			for(int i=0; i<beforeTemplates.getLength(); i++)
			{
				Node beforeTemplate = beforeTemplates.item(i);
				Node afterTemplate = afterTemplates.item(i);
				Node beforeNameAttribute = beforeTemplate.getAttributes().getNamedItem("name");
				if(beforeNameAttribute != null)
				{
					String beforeName = beforeNameAttribute.getNodeValue();
					String aftername = afterTemplate.getAttributes().getNamedItem("name").getNodeValue();
					boolean hasMatch = (beforeTemplate.getAttributes().getNamedItem("match") != null);
					System.out.println("Checking !(\"" + beforeName + "\".equals(\"" + aftername + "\"))" );
					assertFalse("named template '" + beforeName + "' should be renamed", beforeName.equals(aftername));
					NodeList beforeCallsToTemplate = getCallTemplates(beforeName, false);
					if(beforeCallsToTemplate.getLength() > 0)
					{
						System.out.println("Checking calls to template: " + beforeName);
						NodeList afterCallsToTemplate = getCallTemplates(aftername, true);
						assertEquals(beforeCallsToTemplate.getLength(), afterCallsToTemplate.getLength());
						List<String> beforeParamNames = getParams(beforeTemplate);
						for(int j=0; j<afterCallsToTemplate.getLength(); j++)
						{
							List<String> paramNamesInCall = getWithParams(afterCallsToTemplate.item(j));
							for(int k=0; k<beforeParamNames.size(); k++)
							{
								String nextName = beforeParamNames.get(k);
								boolean paramNameInSource = paramNamesInCall.contains(nextName);
								if(!hasMatch)
								{
									if(paramNameInSource)
									{
										String message = "Calling template " + beforeName + " (" + aftername + ")";
										message += " with param " + nextName + " not renamed ";
										fail(message);
									}
								}
								else if(!paramNameInSource)
								{
									System.out.println("SUSPICIOUS: did not find param '" + "' in call to " + beforeName);
								}
							}
						}
					}
					else
					{
						//System.out.println("No calls to template: " + beforeName);
					}
				}
			}
		}
		catch(XPathExpressionException ex)
		{
			fail(ex.getMessage());
		}
	}

	private List<String> getParams(Node template)
	{
		List<String> result = new ArrayList<String>();
		NodeList kids = template.getChildNodes();
		for(int i=0; i<kids.getLength(); i++)
		{
			Node kid = kids.item(i);
			if(kid.getNodeType() == Node.ELEMENT_NODE)
			{
				String tagName = ((Element) kid).getTagName();
				if("xsl:param".equals(tagName))
				{
					result.add(kid.getAttributes().getNamedItem("name").getNodeValue());
				}
			}
		}
		return result;
	}

	private List<String> getWithParams(Node callTemplate)
	{
		List<String> result = new ArrayList<String>();
		NodeList kids = callTemplate.getChildNodes();
		for(int i=0; i<kids.getLength(); i++)
		{
			Node kid = kids.item(i);
			if(kid.getNodeType() == Node.ELEMENT_NODE)
			{
				String tagName = ((Element) kid).getTagName();
				if("xsl:with-param".equals(tagName))
				{
					result.add(kid.getAttributes().getNamedItem("name").getNodeValue());
				}
			}
		}
		return result;
	}

	private NodeList getCallTemplates(String templateName, boolean minified)
	{
		String query = "//xsl:call-template[@name='" + templateName + "']";
		NodeList result = null;
		try
		{
			if(minified)
			{
				result = XpathUtils.executeQuery(XslMinTestUtils.getResultXsl(), query);
			}
			else
			{
				result = XpathUtils.executeQuery(XslMinTestUtils.getSourceXsl(), query);
			}
		}
		catch(XPathExpressionException ex)
		{
			fail(ex.getMessage());
		}
		return result;
	}


}
