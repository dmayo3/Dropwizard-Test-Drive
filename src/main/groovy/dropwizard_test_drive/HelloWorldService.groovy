package dropwizard_test_drive

import com.yammer.dropwizard.Service
import com.yammer.dropwizard.assets.AssetsBundle
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.views.ViewBundle
import com.yammer.metrics.Metrics
import com.yammer.metrics.core.Gauge

import dropwizard_test_drive.health.TemplateHealthCheck
import dropwizard_test_drive.resources.FileResource
import dropwizard_test_drive.resources.HelloWorldResource

class HelloWorldService extends Service<HelloWorldConfiguration> {

    public static void main(String[] args) {
        new HelloWorldService().run(args)
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.name = 'hello-world'

        bootstrap.addBundle(new AssetsBundle("/assets/", "/test/"))
        bootstrap.addBundle(new ViewBundle())
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) throws Exception {

        HelloWorldResource helloWorldResource = new HelloWorldResource(configuration.template, configuration.defaultName)

        environment.addResource helloWorldResource
        environment.addResource new FileResource()

        environment.addHealthCheck new TemplateHealthCheck(configuration.template)

        Metrics.newGauge HelloWorldResource, 'sayings-generated', new Gauge<Long>() {
                    @Override
                    public Long value() {
                        helloWorldResource.counter.get()
                    }
                }

        DropwizardHateoasSetup.configure environment.jerseyResourceConfig
    }
}
