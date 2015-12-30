<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
 
<!-- END MAIN CONTENT AREA -->
</td>
            </tr>
        </table></td></tr></table>
     
  

<!-- Footer -->
<c:set var="urlPrefix" value=""/>
<c:set var="requestFromSpringController" value="${param.isSpringControllerFooter}" />
<c:if test="${requestFromSpringController == '1' }">
    <c:set var="urlPrefix" value="../"/>
</c:if>
<c:if test="${requestFromSpringController == '2' }">
    <c:set var="urlPrefix" value="../../"/>
</c:if>
 <script type="text/javascript" src="includes/wz_tooltip/wz_tooltip.min.js"></script>



<table border="0" cellpadding="0" width="100%"  >
            <tr>
                <td class="footer_bottom" style="width:200px">
                <a href="http://www.openclinica.com" target="new"><fmt:message key="openclinica_portal" bundle="${resword}"/></a>
                &nbsp;&nbsp;&nbsp;
                <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1')"><fmt:message key="help" bundle="${resword}"/></a>
                &nbsp;&nbsp;&nbsp;
                <a href="${urlPrefix}Contact"><fmt:message key="contact" bundle="${resword}"/></a>
                </td>
                <td class="footer_bottom" >
				<fmt:message key="footer.license.1" bundle="${resword}"/> 
               <fmt:message key="footer.license.2" bundle="${resword}"/>
			   <fmt:message key="footer.license.3" bundle="${resword}"/></td>
				
                <td  class="footer_bottom" style="width:200px;">
				
                    <!-- <a href="javascript:void(0)" onmouseover="Tip('<fmt:message key="footer.tooltip" bundle="${resword}"/>')" onmouseout="UnTip()">
                    -->
					<div id="footer_tooltip">
                    <span onmouseover="Tip('<fmt:message key="footer.tooltip" bundle="${resword}"/>')" onmouseout="UnTip()" style="color: #789EC5;"  >
                      <fmt:message key="footer.edition.2" bundle="${resword}" /></span>
					  </div>
					 <div  id="version"></div><fmt:message key="Version_release" bundle="${resword}"/> </div>
                </td>
            </tr>
        </table>

<!-- End Footer -->

        

</body>

</html>
