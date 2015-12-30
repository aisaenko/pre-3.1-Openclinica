/**
 * 
 */
package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.akaza.openclinica.bean.logic.SkipRuleBean;

/**
 * @author frazina
 *
 */
public class DisplaySkipRuleBean {
	protected List<ItemBean> triggerItems=new ArrayList<ItemBean>();
	protected List jsExpression=new ArrayList();
	protected Set<Entry<ItemBean, String>> assignments=null;// list of name-value pairs
	protected SkipRuleBean skipRule=null;

	/**
	 * @param skipRule
	 */
	public DisplaySkipRuleBean(SkipRuleBean skipRule) {
		super();
		this.skipRule = skipRule;
	}
	/**
	 * @return the assignments
	 */
	public Set<Entry<ItemBean, String>> getAssignments() {
		return assignments;
	}
	/**
	 * @param assignments the assignments to set
	 */
	public void setAssignments(Set<Entry<ItemBean, String>> assignments) {
		this.assignments = assignments;
	}
	/**
	 * @return the jsExpression
	 */
	public List getJsExpression() {
		return jsExpression;
	}
	/**
	 * @param jsExpression the jsExpression to set
	 */
	public void setJsExpression(List jsExpression) {
		this.jsExpression = jsExpression;
	}
	/**
	 * @return the triggerItems
	 */
	public List<ItemBean> getTriggerItems() {
		return triggerItems;
	}
	/**
	 * @param triggerItems the triggerItems to set
	 */
	public void setTriggerItems(List<ItemBean> triggerItems) {
		this.triggerItems = triggerItems;
	}
	/**
	 * @return the skipRule
	 */
	public SkipRuleBean getSkipRule() {
		return skipRule;
	}
	/**
	 * @param skipRule the skipRule to set
	 */
	public void setSkipRule(SkipRuleBean skipRule) {
		this.skipRule = skipRule;
	}
}
