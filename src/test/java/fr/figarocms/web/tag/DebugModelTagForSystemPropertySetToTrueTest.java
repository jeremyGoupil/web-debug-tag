package fr.figarocms.web.tag;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class DebugModelTagForSystemPropertySetToTrueTest extends AbstractDebugModelTagForSystemProperty{





    @Before
    public void setUp() throws Exception {
        super.setUp();
        overrideConstantField(field,true);
    }



        @Test
    public void test_system_get_property_set_to_true_and_outputDebugModelInJSON_called_three_times() throws Exception {



            mockDebugModelTag.doStartTag();
           mockDebugModelTag.doStartTag();
           mockDebugModelTag.doStartTag();

           verify(mockDebugModelTag,times(3)).outputDebugModelInJSON();


    }


}
