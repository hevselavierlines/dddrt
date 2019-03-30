package com.baselet.element.ddd;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;

public class VariableNameHelper {
	public static String[] KEYWORDS = new String[] {
			"abstract",
			"assert",
			"boolean",
			"break",
			"byte",
			"case",
			"catch",
			"char",
			"char",
			"class",
			"const",
			"continue",
			"default",
			"do",
			"double",
			"else",
			"enum",
			"exports",
			"extends",
			"false",
			"final",
			"finally",
			"float",
			"for",
			"goto",
			"if",
			"implements",
			"import",
			"instanceof",
			"int",
			"interface",
			"long",
			"module",
			"native",
			"new",
			"null",
			"package",
			"private",
			"protected",
			"public",
			"requires",
			"return",
			"short",
			"static",
			"staticfp",
			"super",
			"switch",
			"synchronized",
			"this",
			"throw",
			"throws",
			"transient",
			"true",
			"try",
			"var",
			"void",
			"volatile",
			"while"
	};

	public static boolean validateVariableName(String variableName) {
		RegularExpression regex = new RegularExpression("^[a-zA-Z\\_]\\w*$");
		if (!regex.matches(variableName)) {
			return false;
		}

		for (String keyword : KEYWORDS) {
			if (keyword.equals(variableName)) {
				return false;
			}
		}
		return true;
	}

}
