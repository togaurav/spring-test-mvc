/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.test.web.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

/**
 * A default implementation of ResponseCreator with builder-style methods.
 *
 * @author Rossen Stoyanchev
 */
class DefaultResponseCreator implements ResponseCreator {

	private byte[] body;

	private Resource bodyResource;

	private final HttpHeaders headers = new HttpHeaders();

	private HttpStatus statusCode;


	/**
	 * Create an instance with the given status code.
	 */
	public DefaultResponseCreator(HttpStatus statusCode) {
		Assert.notNull(statusCode);
		this.statusCode = statusCode;
	}

	public DefaultResponseCreator body(String body) {
		this.body = body.getBytes(Charset.forName("UTF-8"));
		return this;
	}

	public DefaultResponseCreator body(byte[] body) {
		this.body = body;
		return this;
	}

	public DefaultResponseCreator body(Resource bodyResource) {
		this.bodyResource = bodyResource;
		return this;
	}

	/**
	 * Set the {@code Content-Type} header.
	 */
	public DefaultResponseCreator contentType(MediaType mediaType) {
		if (mediaType != null) {
			this.headers.setContentType(mediaType);
		}
		return this;
	}

	/**
	 * Set the {@code Location} header.
	 */
	public DefaultResponseCreator location(URI location) {
		this.headers.setLocation(location);
		return this;
	}

	/**
	 * Copy all given headers.
	 */
	public DefaultResponseCreator headers(HttpHeaders headers) {
		for (String headerName : headers.keySet()) {
			for (String headerValue : headers.get(headerName)) {
				this.headers.add(headerName, headerValue);
			}
		}
		return this;
	}

	public ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
		if (this.bodyResource != null ){
			InputStream stream = this.bodyResource.getInputStream();
			return new MockClientHttpResponse(stream, this.headers, this.statusCode);
		}
		else {
			return new MockClientHttpResponse(this.body, this.headers, this.statusCode);
		}
	}

}