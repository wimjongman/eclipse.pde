/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.pde.internal.core.cheatsheet.simple;

import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.pde.internal.core.XMLPrintHandler;
import org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSModel;
import org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSModelFactory;
import org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSRunContainerObject;
import org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSSubItem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SimpleCSSubItem
 *
 */
public class SimpleCSSubItem extends SimpleCSObject implements ISimpleCSSubItem {

	
	/**
	 * Attribute:  label
	 */
	private String fLabel;
	
	/**
	 * Attribute:  skip
	 */
	private boolean fSkip;
	
	/**
	 * Attribute:  when
	 */
	private String fWhen;
	
	/**
	 * Elements:  action, command, perform-when
	 */
	private ISimpleCSRunContainerObject fExecutable;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param model
	 */
	public SimpleCSSubItem(ISimpleCSModel model) {
		super(model);
		reset();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSSubItem#getExecutable()
	 */
	public ISimpleCSRunContainerObject getExecutable() {
		return fExecutable;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSSubItem#getLabel()
	 */
	public String getLabel() {
		return fLabel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSSubItem#getSkip()
	 */
	public boolean getSkip() {
		return fSkip;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSSubItem#getWhen()
	 */
	public String getWhen() {
		return fWhen;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSSubItem#setExecutable(org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSRunContainerObject)
	 */
	public void setExecutable(ISimpleCSRunContainerObject executable) {
		fExecutable = executable;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSSubItem#setLabel(java.lang.String)
	 */
	public void setLabel(String label) {
		fLabel = label;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSSubItem#setSkip(boolean)
	 */
	public void setSkip(boolean skip) {
		fSkip = skip;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSSubItem#setWhen(java.lang.String)
	 */
	public void setWhen(String when) {
		fWhen = when;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSObject#parse(org.w3c.dom.Node)
	 */
	public void parse(Element element) {
		// Process label attribute
		fLabel = element.getAttribute(ATTRIBUTE_LABEL);
		// Process skip attribute
		if (element.getAttribute(ATTRIBUTE_SKIP).compareTo(
				ATTRIBUTE_VALUE_TRUE) == 0) {
			fSkip = true;
		}
		// Process when attribute
		fWhen = element.getAttribute(ATTRIBUTE_WHEN);
		// Process children
		NodeList children = element.getChildNodes();
		ISimpleCSModelFactory factory = getModel().getFactory();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String name = child.getNodeName();
				Element childElement = (Element)child;

				if (name.equals(ELEMENT_ACTION)) {
					fExecutable = factory.createSimpleCSAction();
					fExecutable.parse(childElement);
				} else if (name.equals(ELEMENT_COMMAND)) {
					fExecutable = factory.createSimpleCSCommand();
					fExecutable.parse(childElement);
				} else if (name.equals(ELEMENT_PERFORM_WHEN)) {
					fExecutable = factory.createSimpleCSPerformWhen();
					fExecutable.parse(childElement);
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IWritable#write(java.lang.String, java.io.PrintWriter)
	 */
	public void write(String indent, PrintWriter writer) {
		
		StringBuffer buffer = new StringBuffer();
		String newIndent = indent + XMLPrintHandler.XML_INDENT;
		
		try {
			// Print subitem element
			buffer.append(ELEMENT_SUBITEM); //$NON-NLS-1$
			// Print label attribute
			if ((fLabel != null) && 
					(fLabel.length() > 0)) {
				buffer.append(XMLPrintHandler.wrapAttributeForPrint(
						ATTRIBUTE_LABEL, fLabel));
			}
			// Print skip attribute
			buffer.append(XMLPrintHandler.wrapAttributeForPrint(
					ATTRIBUTE_SKIP, new Boolean(fSkip).toString()));
			// Print when attribute
			if ((fWhen != null) && 
					(fWhen.length() > 0)) {
				buffer.append(XMLPrintHandler.wrapAttributeForPrint(
						ATTRIBUTE_WHEN, fWhen));
			}
			// Start element
			XMLPrintHandler.printBeginElement(writer, buffer.toString(),
					indent, false);
			// Print action | command | perform-when element
			if (fExecutable != null) {
				fExecutable.write(newIndent, writer);
			}
			// End element
			XMLPrintHandler.printEndElement(writer, ELEMENT_SUBITEM, indent);
			
		} catch (IOException e) {
			// Suppress
			//e.printStackTrace();
		} 				
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSObject#reset()
	 */
	public void reset() {
		fLabel = null;
		fSkip = false;
		fWhen = null;
		fExecutable = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSObject#getType()
	 */
	public int getType() {
		return TYPE_SUBITEM;
	}

}
