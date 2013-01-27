package dropwizard_test_drive.views

import com.yammer.dropwizard.views.View

import dropwizard_test_drive.api.Saying

class FooBarView extends View {

    Saying saying

    FooBarView() {
        super('foobar.mustache')
    }
}
