package pl.matisoft.soy.bundle;

import com.google.template.soy.msgs.SoyMsgBundle;

import java.io.IOException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 22:37
 */
public class EmptySoyMsgBundleResolver implements SoyMsgBundleResolver {

    @Override
    public SoyMsgBundle resolve(Locale locale) throws IOException {
        return null;
    }

}
