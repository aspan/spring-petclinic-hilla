package org.springframework.samples.petclinic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(1)
public class FixValidationResponseWithBrackets implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(FixValidationResponseWithBrackets.class);

	@Override
	public void doFilter(
		ServletRequest request,
		ServletResponse response,
		FilterChain chain) throws IOException, ServletException {
		var req = (HttpServletRequest) request;
		var res = (HttpServletResponse) response;
		var responseWrapper = new ContentCachingResponseWrapper(res);

		chain.doFilter(req, responseWrapper);

		var responseBody = new String(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());

		if (responseBody.contains("com.vaadin.hilla.exception.EndpointValidationException")) {
			var newResponseBody = responseBody.replaceAll("\\[(\\d+)]", ".$1");
			LOGGER.debug(
				"Response :{}\nBody:\n{}\nNew Body:\n{}",
				res.getContentType(),
				responseBody,
				newResponseBody
			);
			responseWrapper.resetBuffer();
			responseWrapper.getOutputStream().write(newResponseBody.getBytes(StandardCharsets.UTF_8));
		}
		responseWrapper.copyBodyToResponse();
	}
}
