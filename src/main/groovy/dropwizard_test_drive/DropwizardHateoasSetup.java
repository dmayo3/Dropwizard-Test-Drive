/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dropwizard_test_drive;

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jaxrs.hateoas.CollectionWrapperStrategy;
import com.jayway.jaxrs.hateoas.HateoasContextProvider;
import com.jayway.jaxrs.hateoas.HateoasLinkInjector;
import com.jayway.jaxrs.hateoas.HateoasVerbosity;
import com.jayway.jaxrs.hateoas.core.HateoasResponse.HateoasResponseBuilder;
import com.jayway.jaxrs.hateoas.core.jersey.JerseyHateoasApplication;
import com.jayway.jaxrs.hateoas.core.jersey.JerseyHateoasContextFilter;
import com.jayway.jaxrs.hateoas.support.DefaultCollectionWrapperStrategy;
import com.jayway.jaxrs.hateoas.support.DefaultHateoasViewFactory;
import com.jayway.jaxrs.hateoas.support.StrategyBasedLinkInjector;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.core.spi.scanning.ScannerListener;

import dropwizard_test_drive.resources.HelloWorldResource;

/**
 * This is a hacked version of {@link JerseyHateoasApplication} that allows us
 * to integrate with Dropwizard.
 */
public class DropwizardHateoasSetup {

    private static final Logger logger = LoggerFactory.getLogger(DropwizardHateoasSetup.class);

    @SuppressWarnings("unchecked")
    public static void configure(ResourceConfig resourceConfig) {
        HateoasLinkInjector<Object> linkInjector = new StrategyBasedLinkInjector();
        CollectionWrapperStrategy collectionWrapperStrategy = new DefaultCollectionWrapperStrategy();

        Set<Class<?>> allClasses = getResourceClasses();

        logger.info("All classes for link scanning: {}", allClasses);

        for (Class<?> clazz : allClasses) {
            HateoasContextProvider.getDefaultContext().mapClass(clazz);
        }

        HateoasResponseBuilder.configure(linkInjector, collectionWrapperStrategy, new DefaultHateoasViewFactory());
        HateoasVerbosity.setDefaultVerbosity(HateoasVerbosity.MAXIMUM);

        JerseyHateoasContextFilter filter = new JerseyHateoasContextFilter();

        resourceConfig.getContainerRequestFilters().add(filter);
        resourceConfig.getContainerResponseFilters().add(filter);
    }

    private static Set<Class<?>> getResourceClasses() {
        String resourcesPackageName = HelloWorldResource.class.getPackage().getName();

        PackageNamesScanner resourceScanner = new PackageNamesScanner(new String[] { resourcesPackageName });

        final Set<Class<?>> resourceClasses = newHashSet();

        resourceScanner.scan(new ScannerListener() {

            public boolean onAccept(String name) {
                return name.endsWith(".class");
            }

            public void onProcess(String name, InputStream in) throws IOException {
                try {
                    resourceClasses.add(Class.forName(name.replace(".class", "").replace("/", ".")));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        return resourceClasses;
    }

}
