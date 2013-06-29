package pl.matisoft.soy.example;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import pl.matisoft.soy.SoyTemplateViewResolver;
import pl.matisoft.soy.ajax.SoyAjaxController;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.compile.DefaultTofuCompiler;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.data.DefaultToSoyDataConverter;
import pl.matisoft.soy.data.ToSoyDataConverter;
import pl.matisoft.soy.global.EmptyGlobalModelResolver;
import pl.matisoft.soy.global.GlobalModelResolver;
import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;
import pl.matisoft.soy.global.compile.EmptyCompileTimeGlobalModelResolver;
import pl.matisoft.soy.locale.EmptyLocaleProvider;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.render.DefaultTemplateRenderer;
import pl.matisoft.soy.render.TemplateRenderer;
import pl.matisoft.soy.template.DefaultTemplateFilesResolver;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 27/06/2013
 * Time: 23:02
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"pl.matisoft.soy.example"})
public class SoyConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private javax.servlet.ServletContext context;

    private boolean debugOn = true;

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.TEXT_HTML)
                .favorPathExtension(true)
                .favorParameter(true)
                .ignoreAcceptHeader(true)
                .mediaType("html", MediaType.TEXT_HTML)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
    }

    @Bean
    public DefaultTemplateFilesResolver templateFilesResolver() {
        final DefaultTemplateFilesResolver defaultTemplateFilesResolver = new DefaultTemplateFilesResolver();
        defaultTemplateFilesResolver.setDebugOn(debugOn);
        defaultTemplateFilesResolver.setRecursive(true);
        defaultTemplateFilesResolver.setTemplatesLocation(new ServletContextResource(context, "/WEB-INF/templates"));

        return defaultTemplateFilesResolver;
    }

    @Bean
    public CompileTimeGlobalModelResolver compileTimeGlobalModelResolver() {
        return new EmptyCompileTimeGlobalModelResolver();
    }

    @Bean
    public GlobalModelResolver globalModelResolver() {
        return new EmptyGlobalModelResolver();
    }

    @Bean
    public LocaleProvider localeProvider() {
        return new EmptyLocaleProvider();
    }

    @Bean
    public EmptySoyMsgBundleResolver soyMsgBundleResolver() {
        return new EmptySoyMsgBundleResolver();
    }

    @Bean
    public ToSoyDataConverter toSoyDataConverter() {
        return new DefaultToSoyDataConverter();
    }

    public TemplateRenderer templateRenderer() {
        final DefaultTemplateRenderer defaultTemplateRenderer = new DefaultTemplateRenderer();
        defaultTemplateRenderer.setDebugOn(debugOn);
        defaultTemplateRenderer.setGlobalModelResolver(globalModelResolver());
        defaultTemplateRenderer.setLocaleProvider(localeProvider());
        defaultTemplateRenderer.setSoyMsgBundleResolver(soyMsgBundleResolver());
        defaultTemplateRenderer.setToSoyDataConverter(toSoyDataConverter());

        return defaultTemplateRenderer;
    }

    @Bean
    public TofuCompiler tofuCompiler() {
        final DefaultTofuCompiler defaultTofuCompiler = new DefaultTofuCompiler();
        defaultTofuCompiler.setCompileTimeGlobalModelResolver(compileTimeGlobalModelResolver());
        defaultTofuCompiler.setDebugOn(debugOn);

        return defaultTofuCompiler;
    }

    @Bean
    public ViewResolver viewResolver() {
        final SoyTemplateViewResolver soyTemplateViewResolver = new SoyTemplateViewResolver();
        soyTemplateViewResolver.setDebugOn(debugOn);
        soyTemplateViewResolver.setTemplateFilesResolver(templateFilesResolver());
        soyTemplateViewResolver.setTemplateRenderer(templateRenderer());
        soyTemplateViewResolver.setTofuCompiler(tofuCompiler());

        return soyTemplateViewResolver;
    }

    @Bean
    public FactoryBean contentNegotiationManagerFactoryBean() {
        final Properties props = new Properties();
        props.put("html", "text/html");
        props.put("json", "application/json");
        props.put("xml", "application/xml");

        final ContentNegotiationManagerFactoryBean contentNegotiationManagerFactoryBean = new ContentNegotiationManagerFactoryBean();
        contentNegotiationManagerFactoryBean.setFavorPathExtension(true);
        contentNegotiationManagerFactoryBean.setFavorParameter(true);
        contentNegotiationManagerFactoryBean.setMediaTypes(props);
        contentNegotiationManagerFactoryBean.setIgnoreAcceptHeader(true);

        return contentNegotiationManagerFactoryBean;
    }

    @Bean
    public ViewResolver contentNegotiatingViewResolver() {
        final ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
//        try {
        //contentNegotiatingViewResolver.setContentNegotiationManager((ContentNegotiationManager) contentNegotiationManagerFactoryBean().getObject());
        contentNegotiatingViewResolver.setViewResolvers(Lists.newArrayList(viewResolver()));
        contentNegotiatingViewResolver.setDefaultViews(Lists.<View>newArrayList(new MappingJacksonJsonView()));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        return contentNegotiatingViewResolver;
    }

    @Bean
    public SoyAjaxController soyAjaxController() {
        final SoyAjaxController soyAjaxController = new SoyAjaxController();
        soyAjaxController.setDebugOn(debugOn);
        soyAjaxController.setLocaleProvider(localeProvider());
        soyAjaxController.setSoyMsgBundleResolver(soyMsgBundleResolver());
        soyAjaxController.setTemplateFilesResolver(templateFilesResolver());
        soyAjaxController.setTofuCompiler(tofuCompiler());

        return soyAjaxController;
    }

}
