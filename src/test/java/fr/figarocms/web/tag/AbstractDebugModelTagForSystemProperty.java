package fr.figarocms.web.tag;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

public abstract class AbstractDebugModelTagForSystemProperty {

@Spy
    protected DebugModelTag mockDebugModelTag = new DebugModelTag();



    protected MockHttpServletResponse response;

    @Mock
    protected MockPageContext pageContext;

    protected MockHttpServletRequest request;

    protected HttpSession session;

    protected MockServletContext servletContext;

    protected static Field field = null;


     static {
        try {
            field = DebugModelTag.class.getDeclaredField("DEBUG_FLAG");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

@Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        session = request.getSession(true);
        servletContext = new MockServletContext();
        pageContext = new MockPageContext(servletContext, request, response);
        mockDebugModelTag.setPageContext(pageContext);
}

      public void overrideConstantField(final Field field,Object value) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null,value );
    }
}
