package soy.config;

import com.google.template.soy.jssrc.SoyJsSrcOptions;
import org.springframework.core.io.ClassPathResource;
import soy.bundle.EmptySoyMsgBundleResolver;
import soy.bundle.SoyMsgBundleResolver;
import soy.compile.DefaultTofuCompiler;
import soy.compile.TofuCompiler;
import soy.data.DefaultToSoyDataConverter;
import soy.data.ToSoyDataConverter;
import soy.global.compile.CompileTimeGlobalModelResolver;
import soy.global.compile.EmptyCompileTimeGlobalModelResolver;
import soy.locale.EmptyLocaleResolver;
import soy.locale.LocaleResolver;
import soy.global.EmptyGlobalModelResolver;
import soy.global.GlobalModelResolver;
import soy.template.DefaultTemplateFilesResolver;
import soy.template.TemplateFilesResolver;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 20:40
 */
public class DefaultSoyViewConfig implements SoyViewConfig {

    public static final String DEFAULT_ENCODING = "utf-8";

    private boolean isDebugOn;

    private LocaleResolver localeResolver = new EmptyLocaleResolver();

    private TofuCompiler tofuCompiler = new DefaultTofuCompiler();

    private SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    private TemplateFilesResolver templateFilesResolver = new DefaultTemplateFilesResolver(new ClassPathResource("/WEB-INF/templates"), true);

    private ToSoyDataConverter toSoyDataConverter = new DefaultToSoyDataConverter();

    private GlobalModelResolver globalModelResolver = new EmptyGlobalModelResolver();

    private CompileTimeGlobalModelResolver compileTimeGlobalModelResolver = new EmptyCompileTimeGlobalModelResolver();

    private SoyJsSrcOptions soyJsSrcOptions = new SoyJsSrcOptions();

    private String encoding = DEFAULT_ENCODING;

    public DefaultSoyViewConfig() {
    }

    public String getEncoding() {
        return encoding;
    }

    public CompileTimeGlobalModelResolver getCompileTimeGlobalModelResolver() {
        return compileTimeGlobalModelResolver;
    }

    public void setCompileTimeGlobalModelResolver(CompileTimeGlobalModelResolver compileTimeGlobalModelResolver) {
        this.compileTimeGlobalModelResolver = compileTimeGlobalModelResolver;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setDebugOn(final boolean debugOn) {
        isDebugOn = debugOn;
    }

    public SoyJsSrcOptions getJsSrcOptions() {
        return soyJsSrcOptions;
    }

    public GlobalModelResolver getGlobalModelResolver() {
        return globalModelResolver;
    }

    public void setGlobalModelResolver(GlobalModelResolver globalModelResolver) {
        this.globalModelResolver = globalModelResolver;
    }

    public SoyJsSrcOptions getSoyJsSrcOptions() {
        return soyJsSrcOptions;
    }

    public void setSoyJsSrcOptions(final SoyJsSrcOptions soyJsSrcOptions) {
        this.soyJsSrcOptions = soyJsSrcOptions;
    }

    public ToSoyDataConverter getToSoyDataConverter() {
        return toSoyDataConverter;
    }

    public void setToSoyDataConverter(ToSoyDataConverter toSoyDataConverter) {
        this.toSoyDataConverter = toSoyDataConverter;
    }

    public TemplateFilesResolver getTemplateFilesResolver() {
        return templateFilesResolver;
    }

    public void setTemplateFilesResolver(TemplateFilesResolver templateFilesResolver) {
        this.templateFilesResolver = templateFilesResolver;
    }

    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public void setTofuCompiler(TofuCompiler tofuCompiler) {
        this.tofuCompiler = tofuCompiler;
    }

    public void setSoyMsgBundleResolver(SoyMsgBundleResolver soyMsgBundleResolver) {
        this.soyMsgBundleResolver = soyMsgBundleResolver;
    }

    @Override
    public boolean isDebugOn() {
        return isDebugOn;
    }

    @Override
    public LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    @Override
    public TofuCompiler getTofuCompiler() {
        return tofuCompiler;
    }

    @Override
    public SoyMsgBundleResolver getSoyMsgBundleResolver() {
        return soyMsgBundleResolver;
    }

}
