package fr.figarocms.web.tag;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class DebugModelTagForSystemPropertySetToFalseOrNotSetTest extends AbstractDebugModelTagForSystemProperty {
    @Before
    public void setUp() throws Exception {
        super.setUp();
        //When system.setProperty is not called or not set to true, the boolean field is set to false.
        overrideConstantField(field, false);
    }

    @Test
    public void test_system_get_property_set_to_false_and_outputDebugModelInJSON_never_called() throws Exception {

        mockDebugModelTag.doStartTag();
        mockDebugModelTag.doStartTag();
        mockDebugModelTag.doStartTag();

        verify(mockDebugModelTag, never()).outputDebugModelInJSON();
    }
}
