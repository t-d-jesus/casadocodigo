package br.com.casadocodigo.loja.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.google.common.cache.CacheBuilder;

import br.com.casadocodigo.loja.controllers.HomeController;
import br.com.casadocodigo.loja.daos.ProductDAO;
import br.com.casadocodigo.loja.infra.FileSaver;
import br.com.casadocodigo.loja.models.ShoppingCart;


//Dentro do @ComponentScan só colocamos as classes de um pacote que queremos injetar em outras classes
//Ao passar uma classe qualquer de um pacote o spring identifica o pacote e nos permite injetar qualquer
//classe desse pacote

//também informamos que nossas views ficaram dentro da pasta mapeada em /WEB-INF/views/
//informamos também que todos os nossos arquivos html(views) terminaram com a extensao .jsp


@EnableWebMvc
@ComponentScan(basePackageClasses={HomeController.class,ProductDAO.class,FileSaver.class,ShoppingCart.class})
@EnableCaching
public class AppWebConfiguration extends WebMvcConfigurerAdapter{
	@Bean
	public InternalResourceViewResolver internalResourceViewResolver(){
		InternalResourceViewResolver resolver = 
				new InternalResourceViewResolver();
		
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		resolver.setExposedContextBeanNames("shoppingCart");
		
		return resolver;
	}
	
	@Bean
	public MessageSource messageSource(){
		ReloadableResourceBundleMessageSource bundle = 
				new ReloadableResourceBundleMessageSource();
		bundle.setBasename("/WEB-INF/messages");
		bundle.setDefaultEncoding("UTF-8");
		bundle.setCacheSeconds(1);
		return bundle;
	}
	
	@Bean
	public FormattingConversionService mvcConversionService(){
		DefaultFormattingConversionService conversionService =
				new DefaultFormattingConversionService(true);
		
		DateFormatterRegistrar registrar = 
				new DateFormatterRegistrar();
		
		registrar.setFormatter(new DateFormatter("yyyy-MM-dd"));
		registrar.registerFormatters(conversionService);
		return conversionService;
	}
	
	@Bean
	public MultipartResolver multipartResolver(){
		return new StandardServletMultipartResolver();
	}
	
	
		@Override
		public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
			// TODO Auto-generated method stub
				configurer.enable();
		}
	
	
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
		
		
	@Bean
	public CacheManager cacheManager(){
		
		CacheBuilder<Object, Object> builder = 
				CacheBuilder
				.newBuilder()
				.maximumSize(100)
				.expireAfterAccess(5, TimeUnit.MINUTES);
		
		GuavaCacheManager cacheManager = new GuavaCacheManager();
		cacheManager.setCacheBuilder(builder);
		
		return cacheManager;
	}
		
	
	@Bean
	public ViewResolver contentNegotiatingViewResolver(
			ContentNegotiationManager manager){
		List<ViewResolver> resolvers = new ArrayList<ViewResolver>();
		
		 
		resolvers.add(internalResourceViewResolver());
		resolvers.add(new JsonViewResolver());
		
		ContentNegotiatingViewResolver resolver = 
				new ContentNegotiatingViewResolver();
		
		resolver.setViewResolvers(resolvers);
		resolver.setContentNegotiationManager(manager);
		
		return resolver;
		
	}
	
	
	//flavio brasil - flaviowbrasil - scaladores
	
	
}
