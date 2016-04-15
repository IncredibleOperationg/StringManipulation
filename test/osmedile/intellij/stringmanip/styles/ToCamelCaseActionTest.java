package osmedile.intellij.stringmanip.styles;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ToCamelCaseActionTest {

    protected ToCamelCaseAction action;

    @Test
    public void testTransform() throws Exception {
        action = new ToCamelCaseAction(false);
        assertEquals("foo", action.transform("foo"));
		assertEquals("!@#$%^&*)(*&|+!!!!!foo!!!!", action.transform("!@#$%^&*)(*&|+!!!!!FOO!!!!"));
		assertEquals("public", action.transform("PUBLIC"));

        assertEquals("testFlexibleQuery", action.transform("testFLEXIBLE_QUERY"));
        assertEquals("testFlexibleQueryProductsForWorkflowAttachment",
                action.transform("testFlexibleQuery_PRODUCTS_FOR_WORKFLOW_ATTACHMENT"));

        assertEquals("thisIsAText", action.transform("This is a text"));

        //this is ugly but nothing can be done about that.
        assertEquals("whOAhATeSt", action.transform("WhOAh a TeSt"));
        assertEquals("whOAhATeSt", action.transform("WhOAh_a_TeSt"));
        assertEquals("whOAhATeSt", action.transform("WhOAh a_TeSt"));
		assertEquals("'closeBsAlert'", action.transform("'Close Bs Alert'"));
		assertEquals("\"closeBsAlert\"", action.transform("\"Close Bs Alert\""));
    }
}