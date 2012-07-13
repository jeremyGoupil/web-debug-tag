package fr.figarocms.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.web.util.HtmlUtils;

public class DebugModelTag extends TagSupport {
    private static final long serialVersionUID = 4611181048692549740L;

    public static final String SCRIPT_TYPE_TEXT_JAVASCRIPT_START = "<script type=\"text/javascript\">";

    public static final String SCRIPT_END = "</script>";

    /**
     *   Property -Ddebug.jsp = true à mettre dans les  variables de la JVM.
     */
    public static final String DEBUG_JSP_FLAG = "debug.jsp";

    public static final String SINGLE_QUOTE = "'";

    public static final String EMPTY = "";

    public static final String VAR_JS_ATTRIBUTE_VIEWER = "attributeViewer";

    public static final String VAR = "var ";

    public static final String STRING_CLASS_NAME = "java.lang.String";


    protected Map<String, Object> debugModel = Maps.newHashMap();

    protected Map<String, Object> debugSession = Maps.newHashMap();

    protected Map<String, Object> debugRequest = Maps.newHashMap();

    @Override
    public int doStartTag() throws JspException {
        String debugJsp = System.getProperty(DEBUG_JSP_FLAG);

        if (debugJsp != null && Boolean.parseBoolean(debugJsp)) {

            JspWriter out = pageContext.getOut();

            List<Integer> pageContextScope = Lists
                    .newArrayList(Arrays.asList(PageContext.SESSION_SCOPE, PageContext.REQUEST_SCOPE));

            try {
                out.println(SCRIPT_TYPE_TEXT_JAVASCRIPT_START);
                for (Integer scope : pageContextScope) {
                    Enumeration attributeNames = pageContext.getAttributeNamesInScope(scope);

                    while (attributeNames.hasMoreElements()) {
                        String element = attributeNames.nextElement().toString();
                        Object attribute;
                        //@TODO: à refactorer : liste des packages non souhaité dans le web xml par exemple.
                        if (element != null && !element.startsWith("__spring") && !element.startsWith("javax") &&
                                !element.startsWith("org.") && !element.startsWith("com.")) {
                            if (scope == PageContext.REQUEST_SCOPE) {
                                attribute = pageContext.getRequest().getAttribute(element);
                                if (attribute != null) {
                                    addAttributeToMap(element, attribute, debugRequest);
                                }
                            } else {
                                attribute = pageContext.getSession().getAttribute(element);
                                if (attribute != null) {
                                    addAttributeToMap(element, attribute, debugSession);
                                }
                            }
                        }
                    }
                }
                debugModel.put("session", debugSession);
                debugModel.put("request", debugRequest);
                ObjectMapper objectMapper = new ObjectMapper();
                out.println(VAR + VAR_JS_ATTRIBUTE_VIEWER + " = " + objectMapper.writeValueAsString(debugModel).replaceAll(
                        SINGLE_QUOTE, EMPTY) + ";");
                out.println("console.debug("+VAR_JS_ATTRIBUTE_VIEWER+");");
                out.println(SCRIPT_END);
            } catch (IOException e) {
                throw new JspException("IOException while writing data to page" + e.getMessage(), e);
            }
        }

        return SKIP_BODY;
    }

    private void addAttributeToMap(final String element, final Object attribute, Map<String, Object> map) {
        if (attribute.getClass().getCanonicalName().equals(STRING_CLASS_NAME)) {
            map.put(element, HtmlUtils.htmlEscape(attribute.toString()));
        } else {
            map.put(element, attribute);
        }
    }
}
