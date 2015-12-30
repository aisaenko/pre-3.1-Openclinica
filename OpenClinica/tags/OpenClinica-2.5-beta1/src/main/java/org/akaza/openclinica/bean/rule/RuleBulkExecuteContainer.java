/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.bean.rule;

/*
 * @author Krikor Krumlian
 */

public class RuleBulkExecuteContainer {

    String crfVersion;
    String ruleName;

    public RuleBulkExecuteContainer(String crfVersion, String ruleName) {
        super();
        this.crfVersion = crfVersion;
        this.ruleName = ruleName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (crfVersion == null ? 0 : crfVersion.hashCode());
        result = prime * result + (ruleName == null ? 0 : ruleName.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RuleBulkExecuteContainer other = (RuleBulkExecuteContainer) obj;
        if (crfVersion == null) {
            if (other.crfVersion != null)
                return false;
        } else if (!crfVersion.equals(other.crfVersion))
            return false;
        if (ruleName == null) {
            if (other.ruleName != null)
                return false;
        } else if (!ruleName.equals(other.ruleName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return crfVersion + " : " + ruleName;
    }

}
