package pl.matisoft.soy.locale;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:07
 */
public interface LocaleResolver {

    Locale resolveLocale(HttpServletRequest request);

}
