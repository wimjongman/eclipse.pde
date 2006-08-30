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
import org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSDescription;
import org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * SimpleCSDescription
 *
 */
public class SimpleCSDescription extends SimpleCSObject implements
		ISimpleCSDescription {

	
	/**
	 * Content (Element)
	 */
	private String fContent;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param model
	 */
	public SimpleCSDescription(ISimpleCSModel model) {
		super(model);
		reset();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSDescription#getContent()
	 */
	public String getContent() {
		return fContent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSDescription#setContent(java.lang.String)
	 */
	public void setContent(String content) {
		fContent = content;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSObject#parse(org.w3c.dom.Node)
	 */
	public void parse(Element element) {
		// Process content
		element.normalize();
		if (element.getChildNodes().getLength() > 0) {
			Node text = element.getFirstChild();
			if (text.getNodeType() == Node.TEXT_NODE) {
				fContent = ((Text)text).getData().trim();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSObject#reset()
	 */
	public void reset() {
		fContent = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IWritable#write(java.lang.String, java.io.PrintWriter)
	 */
	public void write(String indent, PrintWriter writer) {
		
		String newIndent = indent + XMLPrintHandler.XML_INDENT;
		
		try {
			// Start element
			XMLPrintHandler.printBeginElement(writer, ELEMENT_DESCRIPTION,
					indent, false);
			// Print contents
			if ((fContent != null) &&
					(fContent.length() > 0)) {
				XMLPrintHandler.printText(writer, fContent, newIndent);
			}
			// End element
			XMLPrintHandler.printEndElement(writer, ELEMENT_DESCRIPTION, indent);
		} catch (IOException e) {
			// Suppress
			//e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.icheatsheet.simple.ISimpleCSObject#getType()
	 */
	public int getType() {
		return TYPE_DESCRIPTION;
	}

}
