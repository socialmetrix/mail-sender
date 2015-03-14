package com.socialmetrix.mail;

import java.io.IOException;
import java.net.*;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.samskivert.mustache.Mustache;

public class HttpTemplateRenderer {

	public String renderUrlTemplate(String url, Object data) {
		try {
			String template = Resources.toString(new URL(url), Charsets.UTF_8);
			return this.renderStringTemplate(template, data);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String renderStringTemplate(String template, Object data) {
		return Mustache.compiler().withDelims("{% %}").compile(template).execute(data);
	}

}
