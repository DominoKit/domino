#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${subpackage}.client;

import org.junit.Test;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.progressoft.brix.domino.api.client.annotations.ClientModule;
import ${package}.${subpackage}.client.presenters.${module}Presenter;
import ${package}.${subpackage}.client.requests.${module}ServerRequest;
import ${package}.${subpackage}.shared.request.${module}Request;
import ${package}.${subpackage}.shared.response.${module}Response;
import ${package}.${subpackage}.client.presenters.${module}PresenterSpy;
import ${package}.${subpackage}.client.views.Fake${module}View;
import com.progressoft.brix.domino.test.api.DominoTestCase;

@ClientModule(name="Test${module}")
@RunWith(GwtMockitoTestRunner.class)
public class ${module}ClientModuleTest extends DominoTestCase{

    private ${module}PresenterSpy presenterSpy;
    private Fake${module}View fakeView;

    @Override
    public void setUp() {

        testModule.configureModule(new ${module}ModuleConfiguration());
        testModule.configureModule(new Test${module}ModuleConfiguration());

        testModule.replacePresenter(${module}Presenter.class.getCanonicalName(), () -> {
            presenterSpy=new ${module}PresenterSpy();
            return presenterSpy;
        });

        fakeView=testModule.getView(${module}Presenter.class.getCanonicalName());
    }

    @Test
    public void given${module}Module_whenContributingToMainExtensionPoint_thenShouldReceiveMainContext() {
        assertNotNull(presenterSpy.getMainContext());
    }

    @Test
    public void given${module}ClientModule_when${module}ServerRequestIsSent_thenServerMessageShouldBeRecieved() {

        new ${module}ServerRequest(){
            @Override
            protected void process(${module}Presenter presenter, ${module}Request serverArgs, ${module}Response response) {
                super.process(presenter, serverArgs, response);
                assertEquals("Server message",response.getServerMessage());
            }

            @Override
            public String getKey() {
                return ${module}ServerRequest.class.getCanonicalName();
            }
        }.send();
    }
}
