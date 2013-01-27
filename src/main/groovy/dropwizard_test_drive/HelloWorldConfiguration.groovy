package dropwizard_test_drive

import org.hibernate.validator.constraints.NotEmpty

import com.fasterxml.jackson.annotation.JsonProperty
import com.yammer.dropwizard.config.Configuration

class HelloWorldConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    String template

    @NotEmpty
    @JsonProperty
    String defaultName
}
