package com.googlecode.monkeybrown.xslmin;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Renames named templates and the calls to them with short names.
 * Deletes unused named templates.
 * 
 * @author Rick Brown
 */
public class XslNamedTemplateRenamer extends NodeRenamer
{
	private static final String TEMPLATE_XPATH = "//xsl:template[@name%s]";
	
	@Override
	public NodeList getNodesToRename() throws XPathExpressionException
	{
		return XslMin.xpathUtils.executeQuery(String.format(TEMPLATE_XPATH, ""));
	}

	@Override
	public int renameNode(Node node, String oldName, String newName) throws XPathExpressionException
	{
		NodeList nodes = XslMin.xpathUtils.executeQuery("//xsl:call-template[@name='" + oldName + "']");
	    for(int i = 0, len = nodes.getLength(); i < len; i++) 
	    {
	    	nodes.item(i).getAttributes().getNamedItem("name").setNodeValue(newName);
	    }
	    return nodes.getLength();
		
	}
	
	@Override
	public String getClashTestXpath()
	{
		return String.format(TEMPLATE_XPATH, "='%s'");
	}

	@Override
	public boolean canRemoveNode(Node node)
	{
		return false;
		//Node match = node.getAttributes().getNamedItem("match");
		//return match == null;
	}
}
