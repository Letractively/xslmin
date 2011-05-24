package com.googlecode.monkeybrown.xslmin;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.NodeList;

public class XslGlobalVariableRenamer extends XslLocalVariableRenamer
{
	@Override
	public NodeList getNodesToRename() throws XPathExpressionException
	{
		return XslMin.xpathUtils.executeQuery("//node()[not(ancestor-or-self::xsl:template)]/xsl:variable[@name]");
	}
	
	@Override
	protected String getUsageXpath(String varName)
	{
		String xpath = String.format("descendant::*[not(ancestor::xsl:template[descendant::xsl:param[@name='%s'] | descendant::xsl:variable[@name='%1$s']])]/@*[contains(.,'$%1$s')]", varName);
		return xpath;
	}
}
