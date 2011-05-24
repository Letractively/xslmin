package com.googlecode.monkeybrown.xslmin;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XslLocalParamRenamer extends XslLocalVariableRenamer
{
	/*
	 * Can not rename parameters in templates that have a match attribute. 
	 * The problem is that it is difficult to identify which "apply-templates" calls 
	 * will end up invoking which templates.
	 * 
	 * For example this is an easy case:
	 * 
	 * <xsl:apply-templates select="foo:bar">
	 * 	<xsl:with-param name="foo" select="$bar"/>
	 * </xsl:apply-templates>
	 * 
	 * this will obviously invoke this:
	 * 
	 * <xsl:template match="foo:bar">
	 * 
	 * But in this case it is not so easy to know which templates will be invoked:
	 * 
	 * <xsl:apply-templates select="node()">
	 * 	<xsl:with-param name="foo" select="$bar"/>
	 * </xsl:apply-templates>
	 * 
	 * Or this:
	 * 
	 * <xsl:apply-templates>
	 * 	<xsl:with-param name="foo" select="$bar"/>
	 * </xsl:apply-templates>
	 * 
	 */
	@Override
	public int renameNode(Node node, String oldName, String newName) throws XPathExpressionException
	{
		int renamed = super.renameNode(node, oldName, newName);//this has renamed all the param accessors (i.e. "$name" references)
		NodeList nodes;
		//now we have to rename all the param mutators (i.e. "xsl:with-param" references)
		Node myTemplate = (Node) XslMin.xpathUtils.executeQuery(node, "ancestor::xsl:template", XPathConstants.NODE);//the template which owns this parameter
		Node myTemplatesNameAttribute = myTemplate.getAttributes().getNamedItem("name");
		if(myTemplatesNameAttribute != null)
		{
			//the template is being called with "xsl:call-template"
			String myTemplatesName = myTemplatesNameAttribute.getNodeValue();
			//Now we have the name of the template we need to rename the param name in all calls to the template
			String query = String.format("//xsl:call-template[@name='%s']/xsl:with-param/@name[.='%s']", myTemplatesName, oldName);
			nodes = XslMin.xpathUtils.executeQuery(query);//all the attributes we need to rename
			if(nodes != null)
			{
				renamed += nodes.getLength();
				renameWithParams(nodes, newName);
			}
		}
	    return renamed;
		
	}
	
	@Override
	public NodeList getNodesToRename() throws XPathExpressionException
	{
		return XslMin.xpathUtils.executeQuery("//node()[ancestor-or-self::xsl:template[@name and not(@match)]]/xsl:param[@name]");
	}
	
	private void renameWithParams(NodeList nodes, String newName)
	{
		for(int i = 0, len = nodes.getLength(); i < len; i++) 
	    {
	    	nodes.item(i).setNodeValue(newName);
	    }
	}
}
