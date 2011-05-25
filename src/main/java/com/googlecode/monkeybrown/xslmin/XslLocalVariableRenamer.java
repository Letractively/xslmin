package com.googlecode.monkeybrown.xslmin;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Always call this BEFORE renaming global variables / params.
 * We need to rename starting in the lowest scope and working up.
 * 
 * @author Rick Brown
 */
public class XslLocalVariableRenamer extends NodeRenamer
{
	@Override
	public int renameNode(Node node, String oldName, String newName) throws XPathExpressionException
	{
		int renamed = 0;
		Node scope = node.getParentNode();//the variable is scoped by its parent
		NodeList nodes = XslMin.xpathUtils.executeQuery(scope, getUsageXpath(oldName));
	    for(int i = 0, len = nodes.getLength(); i < len; i++)
	    {
	    	Node next = nodes.item(i);
    		String attributeValue = next.getNodeValue();
    		String matchRe = "\\$\\b" + oldName + "\\b(?![\\'\\-\\._])";
    		if(attributeValue.matches(matchRe));
    		{
    			next.setNodeValue(attributeValue.replaceAll(matchRe, "\\$" + newName));
    			renamed++;
    		}
	    }
	    return renamed;
	}
	
	@Override
	public String getClashTestXpath()
	{
		return "ancestor-or-self::xsl:template[descendant::xsl:variable[@name='%s'] or descendant::xsl:param[@name='%1$s']]";
	}

	@Override
	public boolean canRemoveNode(Node node)
	{
		return false;//TODO
	}
	
	@Override
	public NodeList getNodesToRename() throws XPathExpressionException
	{
		return XslMin.xpathUtils.executeQuery("//xsl:template/descendant::xsl:variable[@name]");
	}
	
	protected String getUsageXpath(String varName)
	{
		return "descendant::*/@*[contains(.,'" + "$" + varName + "')]";
	}
}
