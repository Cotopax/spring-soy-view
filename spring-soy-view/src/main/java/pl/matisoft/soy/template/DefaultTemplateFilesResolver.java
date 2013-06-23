package pl.matisoft.soy.template;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 19:58
 */
public class DefaultTemplateFilesResolver implements TemplateFilesResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTemplateFilesResolver.class);

    //private Resource templatesLocation = new ServletContextResource("/WEB-INF/templates");
    private Resource templatesLocation;

    private boolean recursive = true;

    private boolean debugOn = false;

    private CopyOnWriteArrayList<File> cachedFiles = new CopyOnWriteArrayList<File>();

    public DefaultTemplateFilesResolver() {
    }

    @Override
    public Collection<File> resolve() {
        Preconditions.checkNotNull(templatesLocation, "templatesLocation cannot be null!");

        if (debugOn) {
            final List<File> files = toFiles(templatesLocation);
            logger.debug("Debug on - resolved files:" + files.size());

            return files;
        }

        //no debug
        if (cachedFiles.isEmpty()) {
            final List<File> files = toFiles(templatesLocation);
            logger.debug("templates location:" + templatesLocation);
            logger.debug("Using cache resolve, debug off, files:" + files.size());
            cachedFiles.addAll(files);
        }

        return cachedFiles;
    }

    @Override
    public Optional<File> resolve(final String templateFileName) {
        final Collection<File> files = resolve();

        final File templateFile = Iterables.find(files, new Predicate<File>() {
            @Override
            public boolean apply(@Nullable File file) {
                return file.getName().equalsIgnoreCase(templateFileName + ".soy");
            }
        }, null);

        return Optional.fromNullable(templateFile);
    }

    private @Nonnull List<File> toFiles(final Resource templatesLocation) {
        final List<File> templateFiles = Lists.newArrayList();
        try {
            File baseDirectory = templatesLocation.getFile();
            if (baseDirectory.isDirectory()) {
                templateFiles.addAll(findSoyFiles(baseDirectory, recursive));
            } else {
                throw new IllegalArgumentException("Soy template base directory '" + templatesLocation + "' is not a directory");
            }
        } catch (final IOException e) {
            throw new IllegalArgumentException("Soy template base directory '" + templatesLocation + "' does not exist", e);
        }

        return templateFiles;
    }

    protected @Nonnull List<File> findSoyFiles(final File baseDirectory, final boolean recursive) {
        final List<File> soyFiles = new ArrayList<File>();
        findSoyFiles(soyFiles, baseDirectory, recursive);

        return soyFiles;
    }

    protected void findSoyFiles(final List<File> soyFiles, final File baseDirectory, final boolean recursive) {
        final File[] files = baseDirectory.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".soy")) {
                        soyFiles.add(file);
                    }
                } else if (file.isDirectory() && recursive) {
                    findSoyFiles(soyFiles, file, recursive);
                }
            }
        } else {
            throw new IllegalArgumentException("Unable to retrieve contents of:" + baseDirectory);
        }
    }

    public void setTemplatesLocation(final Resource templatesLocation) {
        this.templatesLocation = templatesLocation;
    }

    public void setRecursive(final boolean recursive) {
        this.recursive = recursive;
    }

    public void setDebugOn(final boolean debugOn) {
        this.debugOn = debugOn;
    }

}
