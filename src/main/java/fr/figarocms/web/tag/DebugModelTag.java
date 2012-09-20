package fr.figarocms.web.tag;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

public class DebugModelTag extends TagSupport {
    private static final long serialVersionUID = 4611181048692549740L;

    public static final String SCRIPT_TYPE_TEXT_JAVASCRIPT_START = "<script type=\"text/javascript\">";

    public static final String SCRIPT_END = "</script>";

    /**
     * Property -Ddebug.jsp = true à mettre dans les  variables de la JVM.
     */
    public static final String DEBUG_JSP_FLAG = "debug.jsp";

    public static final String SINGLE_QUOTE = "'";

    public static final String EMPTY = "";

    public static final String VAR_JS_ATTRIBUTE_VIEWER = "attributeViewer";

    public static final String VAR = "var ";

    public static final String STRING_CLASS_NAME = "java.lang.String";

    private static final String WEBDEBUG_EXCLUDES = "webdebug.excludes";

    private static final String PAGE_REQUEST_KEY = "page";

    private static final String REQUEST_MODEL_KEY = "request";

    private static final String SESSION_MODEL_KEY = "session";

    private static final String APPLICATION_MODEL_KEY = "application";

    private static final String EXCLUDE_PACKAGE_SEPARATOR = ",";

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugModelTag.class);

    private static final String DEBUG_JVM_PARAMETER = System.getProperty(DEBUG_JSP_FLAG);


    private static final boolean DEBUG_FLAG =
            (DEBUG_JVM_PARAMETER != null) && Boolean.parseBoolean(DEBUG_JVM_PARAMETER);

    private static final List<Integer> SCOPES = Lists.newArrayList(
            Arrays.asList(PageContext.PAGE_SCOPE,PageContext.SESSION_SCOPE, PageContext.REQUEST_SCOPE, PageContext.APPLICATION_SCOPE));



    private Map<String, Object> debugModel = Maps.newHashMap();

    private Map<String, Object> debugSession = Maps.newHashMap();
    private  Map<String, Object> debugRequest = Maps.newHashMap();

    private  Map<String, Object> debugPage = Maps.newHashMap();

    private  Map<String, Object> debugApplication = Maps.newHashMap();


    @Override
    public int doStartTag() throws JspException {

        boolean debugOk = DEBUG_FLAG;
         if (debugOk) {
            outputDebugModelInJSON();
           }

        return SKIP_BODY;
    }

    protected void outputDebugModelInJSON() throws JspException {
            
            JspWriter out = pageContext.getOut();

            try {
                out.println(SCRIPT_TYPE_TEXT_JAVASCRIPT_START);
                for (Integer scope : SCOPES) {
                    Enumeration attributeNames = pageContext.getAttributeNamesInScope(scope);

                    while (attributeNames!= null && attributeNames.hasMoreElements()) {
                        String element = attributeNames.nextElement().toString();
                        Object attribute = null;
                        String packagesToExclude = pageContext.getServletContext().getInitParameter(WEBDEBUG_EXCLUDES);
                        List<String> tokenToFilter = Lists.newArrayList();
                        if(packagesToExclude!=null){
                        tokenToFilter = Arrays.asList(packagesToExclude.split(EXCLUDE_PACKAGE_SEPARATOR));
                        }

                        if (element != null && !ignoredPackage(element, tokenToFilter)) {

                            if (scope == PageContext.PAGE_SCOPE) {
                                attribute = pageContext.getAttribute(element);
                                if (attribute != null) {
                                    addAttributeToMap(element, attribute, debugPage);
                                }
                            }else if (scope == PageContext.REQUEST_SCOPE) {
                                attribute = pageContext.getRequest().getAttribute(element);
                                if (attribute != null) {
                                    addAttributeToMap(element, attribute, debugRequest);
                                }
                            } else if (scope == PageContext.SESSION_SCOPE) {
                                final HttpSession session = pageContext.getSession();
                                if (session != null) {
                                attribute = session.getAttribute(element);
                                }
                                if (attribute != null) {
                                    addAttributeToMap(element, attribute, debugSession);
                                }
                            } else if (scope == PageContext.APPLICATION_SCOPE) {
                                attribute = pageContext.getServletContext().getAttribute(element);
                                if (attribute != null) {
                                    addAttributeToMap(element, attribute, debugApplication);
                                }
                            }
                        }
                    }
                }

                debugModel.put(PAGE_REQUEST_KEY, debugPage);
                debugModel.put(REQUEST_MODEL_KEY, debugRequest);
                debugModel.put(SESSION_MODEL_KEY, debugSession);
                debugModel.put(APPLICATION_MODEL_KEY, debugApplication);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
                String debugModelAsJSON = null;
                try {
                    debugModelAsJSON = objectMapper.writeValueAsString(debugModel);
                } catch (Throwable t) {
                    LOGGER.error("error in debugModel serialization in JSON", t);
                }
                outputModelInJSON(out, debugModelAsJSON);
            } catch (IOException e) {
                throw new JspException("IOException while writing data to page" + e.getMessage(), e);
            }

    }

    private void outputModelInJSON(final JspWriter out, final String debugModelAsJSON) throws IOException {
        out.println(VAR + VAR_JS_ATTRIBUTE_VIEWER + " = " +
                Objects.firstNonNull(debugModelAsJSON, "null").replaceAll(SINGLE_QUOTE, EMPTY) + ";");
        out.println("(typeof console === \"undefined\")? {} : console.dir(" + VAR_JS_ATTRIBUTE_VIEWER + ");");
        out.println(SCRIPT_END);
    }

    private void addAttributeToMap(final String element, final Object attribute, Map<String, Object> map) {
        if (attribute.getClass().getCanonicalName().equals(STRING_CLASS_NAME)) {
            map.put(element, HtmlUtils.htmlEscape(attribute.toString()));
        } else {
            map.put(element, attribute);
        }
    }

    private boolean ignoredPackage(final String element, final List<String> tokenToFilter) {
        boolean isIgnored = false;
        for (String packageIgnored : tokenToFilter) {
            if (isIgnored) {
                return isIgnored;
            }
            isIgnored = element.startsWith(packageIgnored);
        }

        return isIgnored;
    }
}
