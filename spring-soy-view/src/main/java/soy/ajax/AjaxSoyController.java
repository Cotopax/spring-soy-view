package soy.ajax;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.template.soy.msgs.SoyMsgBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import soy.SoyUtils;
import soy.compile.TofuCompiler;
import soy.config.AbstractSoyConfigEnabled;
import soy.template.TemplateFilesResolver;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class AjaxSoyController extends AbstractSoyConfigEnabled {

    private static final Logger logger = LoggerFactory.getLogger(AjaxSoyController.class);

    private String cacheControl = "public, max-age=3600";

	private ConcurrentHashMap<File, String> cachedJsTemplates = new ConcurrentHashMap<File, String>();

	public AjaxSoyController() {
	}

	@RequestMapping(value="/soy/{templateFileName}.js", method=GET)
	public ResponseEntity<String> getJsForTemplateFile(@PathVariable String templateFileName, final HttpServletRequest request) throws IOException {
        SoyUtils.checkSoyViewConfig(config);
		if (!config.isDebugOn() && cachedJsTemplates.containsKey(templateFileName)) {
			return prepareResponseFor(cachedJsTemplates.get(templateFileName));
		}

        final File templateFile = getTemplateFileAndAssertExistence(templateFileName);

        logger.info("Debug true - compiling JavaScript template:" + templateFile);

        final String templateContent = compileTemplateAndAssertSuccess(request, templateFile);
        if (!config.isDebugOn()) {
            cachedJsTemplates.putIfAbsent(templateFile, templateContent);
        }

        return prepareResponseFor(templateContent);
    }

	private ResponseEntity<String> prepareResponseFor(final String templateContent) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/javascript");
		headers.add("Cache-Control", config.isDebugOn() ? "no-cache" : cacheControl);

		return new ResponseEntity<String>(templateContent, headers, OK);
	}

	private String compileTemplateAndAssertSuccess(final HttpServletRequest request, File templateFile) throws IOException {
        final Optional<SoyMsgBundle> soyMsgBundle = SoyUtils.soyMsgBundle(config, request);
        final TofuCompiler tofuCompiler = config.getTofuCompiler();

		final List<String> compiledTemplates = tofuCompiler.compileToJsSrc(templateFile, soyMsgBundle.orNull());

		if (compiledTemplates.size() < 1) {
			throw notFound("No compiled templates found");
		}

		return compiledTemplates.get(0);
	}

    private File getTemplateFileAndAssertExistence(final String templateFileName) {
        final TemplateFilesResolver templateFilesResolver = config.getTemplateFilesResolver();
        final Collection<File> files = templateFilesResolver.resolve();

        final File templateFile = Iterables.find(files, new Predicate<File>() {
            @Override
            public boolean apply(@Nullable File file) {
                return file.getName().equalsIgnoreCase(templateFileName + ".soy");
            }
        }, null);

        if (templateFile == null) {
            throw notFound(templateFileName);
        }

		return templateFile;
	}

	private HttpClientErrorException notFound(String file) {
		return new HttpClientErrorException(NOT_FOUND, file);
	}

	public void setCacheControl(final String cacheControl) {
		this.cacheControl = cacheControl;
	}

}
