package dropwizard_test_drive.health

import com.yammer.metrics.core.HealthCheck

class TemplateHealthCheck extends HealthCheck {

    final String template

    public TemplateHealthCheck(String template) {
        super("template")
        this.template = template
    }

    @Override
    protected Result check() throws Exception {
        String saying = String.format(template, "TEST")

        if (!saying.contains("TEST")) {
            return Result.unhealthy("template doesn't include a name")
        }

        return Result.healthy()
    }
}
