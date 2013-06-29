package pl.matisoft.soy.hash;

import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 29.06.13
 * Time: 23:57
 */
public interface HashFileGenerator {

   Optional<String> hash(final Optional<URL> url) throws IOException;

}
