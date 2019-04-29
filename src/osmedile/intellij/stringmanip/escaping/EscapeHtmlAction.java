package osmedile.intellij.stringmanip.escaping;

import osmedile.intellij.stringmanip.AbstractStringManipAction;

/**
 * @author Olivier Smedile
 * @version $Id: EscapeHtmlAction.java 16 2008-03-20 19:21:43Z osmedile $
 */
public class EscapeHtmlAction extends AbstractStringManipAction {

	@Override
	public String transformByLine(String s) {
		return shaded.org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(s);
    }
}