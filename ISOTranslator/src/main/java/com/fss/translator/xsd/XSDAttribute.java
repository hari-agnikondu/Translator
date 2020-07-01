package com.fss.translator.xsd;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Represents an "attribute" schema definition.
 * 
 * @author Harikrishna Agnikondu
 *
 */
@Data
public class XSDAttribute {

	private String name;
	private boolean required;
	private String type;
	private List<String> options = new ArrayList<>();
	private String defaultValue;

}
