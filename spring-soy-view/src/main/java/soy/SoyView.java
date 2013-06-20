package soy;

import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import org.springframework.web.servlet.view.AbstractTemplateView;
import soy.config.SoyViewConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 19/06/2013
 * Time: 23:32
 */
public class SoyView extends AbstractTemplateView {

    private SoyTofu compiledTemplates;

    private String templateName;

    private SoyViewConfig soyViewConfig;

    public SoyView() {
    }

    @Override
    protected void renderMergedTemplateModel(final Map<String, Object> model,
                                             final HttpServletRequest request,
                                             final HttpServletResponse response) throws Exception {
        final Writer writer = response.getWriter();

        final Map<String, ?> soyData = soyViewConfig.getToSoyDataConverter().convert(model);

        final Locale locale = soyViewConfig.getLocaleResolver().resolveLocale(request);

        SoyMsgBundle soyMsgBundle = null;
        if (locale != null) {
            soyMsgBundle = soyViewConfig.getSoyMsgBundleResolver().resolve(locale);
        }

        final SoyTofu.Renderer renderer = compiledTemplates.newRenderer(templateName);
                renderer.setData(soyData);
                if (soyMsgBundle != null) {
                    renderer.setMsgBundle(soyMsgBundle);
                }

                renderer.render(writer);

        writer.flush();
    }

    public void setSoyViewConfig(final SoyViewConfig soyViewConfig) {
        this.soyViewConfig = soyViewConfig;
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public void setCompiledTemplates(final SoyTofu compiledTemplates) {
        this.compiledTemplates = compiledTemplates;
    }

}
