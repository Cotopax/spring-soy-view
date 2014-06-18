package pl.matisoft.soy.view;

import static java.util.Arrays.asList;
import static java.util.Collections.list;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.CollectionUtils.containsAny;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This class is responsible for determining whether or not it can support the
 * requested content types.
 * 
 * @author Abhijit Sarkar
 * 
 */
public class DefaultContentNegotiator implements ContentNegotiator {
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultContentNegotiator.class);

	public static String DEFAULT_FAVORED_PARAMETER_NAME = "format";
	public static String DEFAULT_SUPPORTED_CONTENT_TYPE = "text/html";
	public static String ACCEPT_HEADER = "Accept";

	private List<String> supportedContentTypes;
	private boolean favorParameterOverAcceptHeader;
	private String favoredParameterName;
	private Comparator<List<String>> contentTypeComparator;

	public DefaultContentNegotiator() {
		supportedContentTypes = asList(DEFAULT_SUPPORTED_CONTENT_TYPE);
		favorParameterOverAcceptHeader = false;
		favoredParameterName = DEFAULT_FAVORED_PARAMETER_NAME;
		contentTypeComparator = new DefaultContentTypeComparator();
	}

	public List<String> getSupportedContentTypes() {
		return supportedContentTypes;
	}

	public void setSupportedContentTypes(
			final List<String> supportedContentTypes) {
		this.supportedContentTypes = supportedContentTypes;
	}

	public boolean isFavorParameterOverAcceptHeader() {
		return favorParameterOverAcceptHeader;
	}

	public void setFavorParameterOverAcceptHeader(
			final boolean favorParameterOverAcceptHeader) {
		this.favorParameterOverAcceptHeader = favorParameterOverAcceptHeader;
	}

	public String getFavoredParameterName() {
		return favoredParameterName;
	}

	public void setFavoredParameterName(final String parameterName) {
		this.favoredParameterName = parameterName;
	}

	public Comparator<List<String>> getContentTypeComparator() {
		return contentTypeComparator;
	}

	public void setContentTypeComparator(
			final Comparator<List<String>> contentTypeComparator) {
		this.contentTypeComparator = contentTypeComparator;
	}

	/**
	 * Returns true is able to support the requested content types; false
	 * otherwise.
	 * 
	 * @return True is able to support the requested content types; false
	 *         otherwise.
	 */
	@Override
	public boolean isSupportedContentTypes() {
		final List<String> contentTypes = contentTypes();

		return contentTypeComparator.compare(supportedContentTypes,
				contentTypes) == 0;
	}

	/**
	 * Returns requested content types or default content type if none found.
	 * 
	 * @return Requested content types or default content type if none found.
	 */
	@Override
	public List<String> contentTypes() {
		List<String> contentTypes = null;
		final HttpServletRequest request = getHttpRequest();

		if (favorParameterOverAcceptHeader) {
			contentTypes = asList(getFavoredParameterValue(request));
		} else {
			contentTypes = getAcceptHeaderValues(request);
		}

		if (isEmpty(contentTypes)) {
			contentTypes = asList(DEFAULT_SUPPORTED_CONTENT_TYPE);
		}

		return contentTypes;
	}

	private HttpServletRequest getHttpRequest() {
		final RequestAttributes attrs = getRequestAttributes();
		isInstanceOf(ServletRequestAttributes.class, attrs);

		return ((ServletRequestAttributes) attrs).getRequest();
	}

	private String getFavoredParameterValue(HttpServletRequest request) {
		logger.debug("Retrieving content type from favored parameter: {}.",
				favoredParameterName);

		return request.getParameter(favoredParameterName);
	}

	@SuppressWarnings("unchecked")
	private List<String> getAcceptHeaderValues(HttpServletRequest request) {
		logger.debug("Retrieving content type from header: {}.", ACCEPT_HEADER);

		return list(request.getHeaders(ACCEPT_HEADER));
	}

	private static final class DefaultContentTypeComparator implements
			Comparator<List<String>> {
		private static final int NOT_EQUAL = 1;
		private static final int EQUAL = 0;

		@Override
		public int compare(List<String> supportedContentTypes,
				List<String> contentTypes) {
			logger.debug(
					"Comparing supported content types with request content types: {}, {}.",
					supportedContentTypes, contentTypes);

			if (isEmpty(contentTypes)) {
				return NOT_EQUAL;
			} else if (containsAny(supportedContentTypes, contentTypes)) {
				return EQUAL;
			}

			return NOT_EQUAL;
		}
	}
}
