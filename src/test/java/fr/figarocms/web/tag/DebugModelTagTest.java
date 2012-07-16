package fr.figarocms.web.tag;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

public class DebugModelTagTest {

    @InjectMocks
    private DebugModelTag debugModelTag = new DebugModelTag();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testDoStartTag() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockPageContext pageContext = new MockPageContext(new MockServletContext(), new MockHttpServletRequest(),
                response);
        debugModelTag.setPageContext(pageContext);
        //
        int code = debugModelTag.doStartTag();
        // check return value
        assertThat("doStartTag returned value", code, equalTo(Tag.SKIP_BODY));
        // retrieve data in response :
        assertThat("expected message in writer", response.getContentAsString(), is(StringUtils.EMPTY));
    }
}
