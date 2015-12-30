package org.akaza.openclinica.view.form;

import org.jdom.*;

/**
 * This class has the sole purpose of creating a non-breaking space character for
 * inside a textarea tag, in order to force JDOM to create a non-empty tag (&lt;textarea>
 * &lt;/textarea>, as opposed to &lt;textarea />).
 */
public class NbspaceContent extends Content{
  public String getValue() {
    return "&nbsp;";
  }
}
