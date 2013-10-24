//package pl.matisoft.soy.configurer;
//
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.core.io.Resource;
//import org.springframework.web.context.ServletContextAware;
//import org.springframework.web.context.support.ServletContextResource;
//import pl.matisoft.soy.SoyTemplateViewResolver;
//import pl.matisoft.soy.ajax.SoyAjaxController;
//import pl.matisoft.soy.ajax.auth.AuthManager;
//import pl.matisoft.soy.ajax.auth.ConfigurableAuthManager;
//import pl.matisoft.soy.ajax.auth.PermissableAuthManager;
//import pl.matisoft.soy.ajax.process.OutputProcessor;
//import pl.matisoft.soy.bundle.DefaultSoyMsgBundleResolver;
//import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
//import pl.matisoft.soy.compile.DefaultTofuCompiler;
//import pl.matisoft.soy.compile.TofuCompiler;
//import pl.matisoft.soy.config.SoyViewConfig;
//import pl.matisoft.soy.data.DefaultToSoyDataConverter;
//import pl.matisoft.soy.data.ToSoyDataConverter;
//import pl.matisoft.soy.data.adjust.ModelAdjuster;
//import pl.matisoft.soy.data.adjust.SpringModelAdjuster;
//import pl.matisoft.soy.global.EmptyGlobalModelResolver;
//import pl.matisoft.soy.global.GlobalModelResolver;
//import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;
//import pl.matisoft.soy.global.compile.EmptyCompileTimeGlobalModelResolver;
//import pl.matisoft.soy.locale.AcceptHeaderLocaleProvider;
//import pl.matisoft.soy.locale.LocaleProvider;
//import pl.matisoft.soy.render.DefaultTemplateRenderer;
//import pl.matisoft.soy.render.TemplateRenderer;
//import pl.matisoft.soy.template.DefaultTemplateFilesResolver;
//import pl.matisoft.soy.template.TemplateFilesResolver;
//
//import javax.servlet.ServletContext;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created with IntelliJ IDEA.
// * User: mszczap
// * Date: 18.10.13
// * Time: 21:01
// */
//public class SimpleSoyConfigurer implements InitializingBean, ServletContextAware {
//
//    private boolean debugOn = SoyViewConfig.DEFAULT_DEBUG_ON;
//
//    private String encoding = SoyViewConfig.DEFAULT_ENCODING;
//
//    private ServletContext servletContext;
//
//    private String templatesPath = SoyViewConfig.DEFAULT_TEMPLATE_FILES_PATH;
//
//    private String i18nMessagesPath = SoyViewConfig.DEFAULT_I18N_MESSAGES_PATH;
//
//    private boolean i18nFallbackToEnglish = SoyViewConfig.DEFAULT_I18N_FALLBACK_TO_ENGLISH;
//
//    private boolean recursiveTemplatesSearch = SoyViewConfig.DEFAULT_RECURSIVE_TEMPLATES_SEARCH;
//
//    private String modelAdjusterKey = SoyViewConfig.DEFAULT_MODEL_ADJUSTER_KEY;
//
//    private int futureTimeoutsInSeconds = SoyViewConfig.DEFAULT_FUTURE_TIMEOUT_IN_SECONDS;
//
//    private List<String> allowedTemplates = new ArrayList<String>();
//
//    private boolean templatesAjaxSecurity = false;
//
//    private String cacheControl = SoyViewConfig.DEFAULT_CACHE_CONTROL;
//
//    private String expireHeaders = SoyViewConfig.DEFAULT_EXPIRE_HEADERS;
//
//    private List<String> outputProcessorClasses = new ArrayList<String>();
//
//    private GlobalModelResolver runtimeModelResolver = new EmptyGlobalModelResolver();
//
//    private CompileTimeGlobalModelResolver compileTimeGlobalModelResolver = new EmptyCompileTimeGlobalModelResolver();
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        soyTemplateViewResolver();
//        soyAjaxController();
//    }
//
//    public SoyTemplateViewResolver soyTemplateViewResolver() {
//        final SoyTemplateViewResolver soyTemplateViewResolver = new SoyTemplateViewResolver();
//        soyTemplateViewResolver.setDebugOn(debugOn);
//        soyTemplateViewResolver.setEncoding(encoding);
//        soyTemplateViewResolver.setTofuCompiler(tofuCompiler());
//        soyTemplateViewResolver.setGlobalModelResolver(runtimeModelResolver);
//        soyTemplateViewResolver.setTemplateRenderer(templateRenderer());
//        soyTemplateViewResolver.setLocaleProvider(localeProvider());
//        soyTemplateViewResolver.setSoyMsgBundleResolver(soyMsgBundleResolver());
//        soyTemplateViewResolver.setTemplateFilesResolver(templateFilesResolver());
//        soyTemplateViewResolver.setModelAdjuster(modelAdjuster());
//        //soyTemplateViewResolver.setPrefix();// TODO
//
//        return soyTemplateViewResolver;
//    }
//
//    private SoyAjaxController soyAjaxController() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
//        final SoyAjaxController soyAjaxController = new SoyAjaxController();
//        soyAjaxController.setAuthManager(authManager());
//        soyAjaxController.setTemplateFilesResolver(templateFilesResolver());
//        soyAjaxController.setCacheControl(cacheControl);
//        soyAjaxController.setDebugOn(debugOn);
//        soyAjaxController.setExpiresHeaders(expireHeaders);
//        soyAjaxController.setLocaleProvider(localeProvider());
//        soyAjaxController.setOutputProcessors(outputProcessors());
//        soyAjaxController.setEncoding(encoding);
//        soyAjaxController.setSoyMsgBundleResolver(soyMsgBundleResolver());
//        soyAjaxController.setTofuCompiler(tofuCompiler());
//
//        return soyAjaxController;
//    }
//
//    private List<OutputProcessor> outputProcessors() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
//        final List<Class> classes = new ArrayList<Class>();
//        for (final String outputProcessor : outputProcessorClasses) {
//            classes.add(Class.forName(outputProcessor));
//        }
//
//        final List<OutputProcessor> outputProcessors = new ArrayList<OutputProcessor>();
//
//        for (final Class clazz : classes) {
//            final Object o = clazz.newInstance();
//            if (o instanceof OutputProcessor) {
//                outputProcessors.add((OutputProcessor) o);
//            }
//        }
//
//        return outputProcessors;
//    }
//
//    private AuthManager authManager() {
//        if (templatesAjaxSecurity) {
//            final ConfigurableAuthManager configurableAuthManager = new ConfigurableAuthManager();
//            configurableAuthManager.setAllowedTemplates(allowedTemplates);
//
//            return configurableAuthManager;
//        }
//
//        return new PermissableAuthManager();
//    }
//
//    private ModelAdjuster modelAdjuster() {
//        final SpringModelAdjuster springModelAdjuster = new SpringModelAdjuster();
//        springModelAdjuster.setModelKey(modelAdjusterKey);
//
//        return springModelAdjuster;
//    }
//
//    private TemplateFilesResolver templateFilesResolver() {
//        final DefaultTemplateFilesResolver defaultTemplateFilesResolver = new DefaultTemplateFilesResolver();
//        defaultTemplateFilesResolver.setDebugOn(debugOn);
//        defaultTemplateFilesResolver.setRecursive(recursiveTemplatesSearch);
//        defaultTemplateFilesResolver.setTemplatesLocation(templatesResource());
//
//        return defaultTemplateFilesResolver;
//    }
//
//    private TofuCompiler tofuCompiler() {
//        final DefaultTofuCompiler tofuCompiler = new DefaultTofuCompiler();
//        tofuCompiler.setDebugOn(debugOn);
//        tofuCompiler.setCompileTimeGlobalModelResolver(compileTimeGlobalModelResolver);
//
//        return tofuCompiler;
//    }
//
//    private LocaleProvider localeProvider() {
//        final LocaleProvider localeProvider = new AcceptHeaderLocaleProvider();
//
//        return localeProvider;
//    }
//
//    private SoyMsgBundleResolver soyMsgBundleResolver() {
//        final DefaultSoyMsgBundleResolver defaultSoyMsgBundleResolver = new DefaultSoyMsgBundleResolver();
//        defaultSoyMsgBundleResolver.setDebugOn(debugOn);
//        defaultSoyMsgBundleResolver.setFallbackToEnglish(i18nFallbackToEnglish);
//        defaultSoyMsgBundleResolver.setMessagesPath(i18nMessagesPath);
//
//        return defaultSoyMsgBundleResolver;
//    }
//
//    private TemplateRenderer templateRenderer() {
//        final DefaultTemplateRenderer templateRenderer = new DefaultTemplateRenderer();
//        templateRenderer.setDebugOn(debugOn);
//        templateRenderer.setToSoyDataConverter(toSoyDataConverter());
//
//        return templateRenderer;
//    }
//
//    private ToSoyDataConverter toSoyDataConverter() {
//        final DefaultToSoyDataConverter defaultToSoyDataConverter = new DefaultToSoyDataConverter();
//        defaultToSoyDataConverter.setFutureTimeOutInSeconds(futureTimeoutsInSeconds);
//
//        return defaultToSoyDataConverter;
//    }
//
//    private Resource templatesResource() {
//        return new ServletContextResource(servletContext, templatesPath);
//    }
//
//    @Override
//    public void setServletContext(final ServletContext servletContext) {
//        this.servletContext = servletContext;
//    }
//
//    public void setDebugOn(boolean debugOn) {
//        this.debugOn = debugOn;
//    }
//
//    public void setEncoding(String encoding) {
//        this.encoding = encoding;
//    }
//
//    public void setTemplatesPath(String templatesPath) {
//        this.templatesPath = templatesPath;
//    }
//
//    public void setI18nMessagesPath(String i18nMessagesPath) {
//        this.i18nMessagesPath = i18nMessagesPath;
//    }
//
//    public void setI18nFallbackToEnglish(boolean i18nFallbackToEnglish) {
//        this.i18nFallbackToEnglish = i18nFallbackToEnglish;
//    }
//
//    public void setRecursiveTemplatesSearch(boolean recursiveTemplatesSearch) {
//        this.recursiveTemplatesSearch = recursiveTemplatesSearch;
//    }
//
//    public void setModelAdjusterKey(String modelAdjusterKey) {
//        this.modelAdjusterKey = modelAdjusterKey;
//    }
//
//    public void setFutureTimeoutsInSeconds(int futureTimeoutsInSeconds) {
//        this.futureTimeoutsInSeconds = futureTimeoutsInSeconds;
//    }
//
//    public void setAllowedTemplates(List<String> allowedTemplates) {
//        this.allowedTemplates = allowedTemplates;
//    }
//
//    public void setTemplatesAjaxSecurity(boolean templatesAjaxSecurity) {
//        this.templatesAjaxSecurity = templatesAjaxSecurity;
//    }
//
//    public void setCacheControl(String cacheControl) {
//        this.cacheControl = cacheControl;
//    }
//
//    public void setRuntimeModelResolver(GlobalModelResolver runtimeModelResolver) {
//        this.runtimeModelResolver = runtimeModelResolver;
//    }
//
//    public void setCompileTimeGlobalModelResolver(CompileTimeGlobalModelResolver compileTimeGlobalModelResolver) {
//        this.compileTimeGlobalModelResolver = compileTimeGlobalModelResolver;
//    }
//
//}
