package dropwizard_test_drive.resources

import java.util.concurrent.atomic.AtomicLong

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.google.common.base.Optional
import com.jayway.jaxrs.hateoas.Linkable
import com.jayway.jaxrs.hateoas.core.HateoasResponse
import com.yammer.dropwizard.jersey.params.IntParam
import com.yammer.metrics.annotation.Timed

import dropwizard_test_drive.api.Saying
import dropwizard_test_drive.views.FooBarView

@Path('/hello-world')
class HelloWorldResource {

    static final Logger logger = LoggerFactory.getLogger(HelloWorldResource)

    static final Object[] NO_PARAMS = []

    final String template
    final String defaultName

    final AtomicLong counter = new AtomicLong()

    HelloWorldResource(template, defaultName) {
        this.template = template
        this.defaultName = defaultName
    }

    private def newSaying(name) {
        new Saying(id: counter.incrementAndGet(), content: String.format(template, name.or(defaultName)))
    }

    @GET
    @Produces(['text/html', 'application/json'])
    @Linkable('sayHello.get')
    @Timed
    Response sayHello(@QueryParam("name") Optional<String> name, @Context HttpHeaders headers) {
        logger.info 'HTTP Headers: {}', headers.requestHeaders

        Saying saying = newSaying(name)

        HateoasResponse
            .ok(new FooBarView(saying: saying))
            .link('fileUpload.post', 'upload', NO_PARAMS)
            .link('numberList.get', 'numbers', saying.id)
            .selfLink('sayHello.get', NO_PARAMS)
            .build()
    }

    @GET
    @Path('/foo/{number}')
    @Produces('application/json')
    @Linkable('numberList.get')
    @Timed
    Response numberList(@PathParam("number") IntParam number) {
        def result = []
        number.get().times { result << it + 1 }

        HateoasResponse
            .ok(result)
            .link('fileUpload.post', 'upload', NO_PARAMS)
            .link('sayHello.get', 'hello', NO_PARAMS)
            .selfLink('numberList.get', number.get())
            .build()
    }

}
