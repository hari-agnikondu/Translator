package com.fss.translator.xsd;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.xs.XSElementDeclaration;

import lombok.Data;

/**
 * Represents an "element" schema definition.
 * 
 * @author Harikrishna Agnikondu
 *
 */
@Data
public class XSDElement {

	private String name;
	private XSElementDeclaration xsDeclaration;
	private XSDElement parent;
	private List<XSDAttribute> attributes = new ArrayList<>();
	private List<XSDElement> children = new ArrayList<>();
	private int minOcurrs;
	private boolean maxOcurrsUnbounded;
	private int maxOcurrs;
	private String type;
	private String defaultValue;

	public boolean isMaxOcurrsUnbounded() {
		return maxOcurrsUnbounded;
	}

	public void addChildren(XSDElement child) {
		children.add(child);
	}

	public void addAttribute(XSDAttribute attribute) {
		attributes.add(attribute);
	}
}
