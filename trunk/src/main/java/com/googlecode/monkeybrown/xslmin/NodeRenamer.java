package com.googlecode.monkeybrown.xslmin;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class NodeRenamer
{
	public abstract NodeList getNodesToRename() throws XPathExpressionException;
	public abstract String getClashTestXpath();
	public abstract int renameNode(Node node, String oldName, String newName) throws XPathExpressionException;
	public abstract boolean canRemoveNode(Node node);

	public void rename() throws XPathExpressionException
	{
		NameGenerator nameGenerator = new NameGenerator();
		List<Node> toRemove = new ArrayList<Node>();

		//find all named nodes that need renaming
		NodeList nodes = this.getNodesToRename();

		//loop over each named node
		for(int i = 0, len = nodes.getLength(); i < len; i++)
		{
			Node next = nodes.item(i);
			Node name = next.getAttributes().getNamedItem("name");
			String oldName = name.getNodeValue();
			String newName = nameGenerator.getNextName(next, this.getClashTestXpath());
			if(newName.length() < oldName.length())//only rename if it makes the name shorter
			{
				name.setNodeValue(newName);
				int referers = this.renameNode(next, oldName, newName);
				if(referers < 1 && this.canRemoveNode(next))
				{
					System.out.println("Deleting unused node " + oldName);
					toRemove.add(next);
				}
			}
		}
		NodeRenamer.removeNodes(toRemove);
	}

	/**
	 * Removes each node in the list from its DOM tree
	 * @param toRemove The list of nodes to remove
	 */
	public static void removeNodes(NodeList toRemove)
	{
		for(int i = 0, len = toRemove.getLength(); i < len; i++)
		{
			Node next = toRemove.item(i);
			Node parent = next.getParentNode();
			if(parent != null)
			{
				parent.removeChild(next);
			}
		}
	}

	/**
	 * Removes each node in the list from its DOM tree
	 * @param toRemove The list of nodes to remove
	 */
	public static void removeNodes(List<Node> toRemove)
	{
		for(int i = 0, len = toRemove.size(); i < len; i++)
		{
			Node next = toRemove.get(i);
			Node parent = next.getParentNode();
			if(parent != null)
			{
				parent.removeChild(next);
			}
		}
	}
}
