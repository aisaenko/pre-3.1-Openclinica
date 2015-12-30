package org.akaza.openclinica.view.form;

import java.util.List;
import org.jdom.Element;

/**
 * This interface defines the methods for creating HTML input types. The inputs
 * represent the content for table cells or TD tags, which are implemented as JDOM Elements.
 * Created by IntelliJ IDEA.
 * User: bruceperry
 * Date: May 17, 2007
 */
public interface InputGenerator {
  Element createTextInputTag(Element tdCell, Integer itemId,
                             Integer tabNumber, String defaultValue, boolean isDateType, String dbValue);
  Element createTextareaTag(Element tdCell, Integer itemId, Integer tabNumber, String dbValue, String defaultValue);
  Element createCheckboxTag(Element tdCell, Integer itemId, List options, Integer tabNumber, boolean includeLabel, String dbValue, String defaultValue, boolean isHorizontal);
  Element createRadioButtonTag(Element tdCell, Integer itemId, List options,
                               Integer tabNumber, boolean includeLabel, String dbValue, String defaultValue, boolean isHorizontal);
  Element createSingleSelectTag(Element tdCell,Integer itemId,List options,Integer tabNumber);
  Element createMultiSelectTag(Element tdCell, Integer itemId, List options, Integer tabNumber, String dbValue, String defaultValue);
}
