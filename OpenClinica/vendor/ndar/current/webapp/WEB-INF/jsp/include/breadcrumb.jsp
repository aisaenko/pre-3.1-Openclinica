<%@page import="org.akaza.openclinica.bean.core.Status"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope='session' id='trail' class='java.util.ArrayList'/>

<script language="JavaScript" type="text/javascript">
function reportBug() {
 var bugtrack = "http://dev.openclinica.org/OpenClinica/bug.php?version=2.0 BETA 2&user=";
 var user= "<c:out value="${userBean.name}"/>";
 bugtrack = bugtrack + user+ "&url=" + window.location.href;
 openDocWindow(bugtrack);

}
</script>

<!-- Breadcrumbs -->

<!-- End Breadcrumbs -->

	</td>
	<!-- Help and Report Bugs Buttons -->

				<td valign="top" style="padding-left: 12px; width: 52px;">
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
					   <td><a href="Enterprise" 
							onMouseDown="setImage('bt_Portal','images/bt_Portal_d.gif');"
							onMouseUp="setImage('bt_Portal','images/bt_Portal.gif');"><img 
							name="bt_Portal" src="images/bt_Portal.gif" border="0" alt="OpenClinica Enterprise" title="OpenClinica Enterprise" style="margin-top: 15px; margin-left: 2px; margin-right: 2px; margin-bottom: 2px"></a>
						</td>
					</tr>
					<tr>				
						<td><a href="javascript:openDocWindow('help/index.html')"
							onMouseDown="setImage('bt_Help','images/bt_Help_d.gif');"
							onMouseUp="setImage('bt_Help','images/bt_Help.gif');"><img 
							name="bt_Help" src="images/bt_Help.gif" border="0" alt="Help" title="Help" style="margin-top: 2px; margin-left: 2px; margin-right: 2px; margin-bottom: 2px"></a>
						</td>	
					</tr>
					<tr>					
						<td><a href="javascript:reportBug()"
							onMouseDown="setImage('bt_Support','images/bt_Support_d.gif');"
							onMouseUp="setImage('bt_Support','images/bt_Support.gif');"><img 
							name="bt_Support" src="images/bt_Support.gif" border="0" alt="OpenClinica Feedback" title="OpenClinica Feedback" style="margin-top: 2px; margin-left: 2px; margin-right: 2px; margin-bottom: 15px"></a>
						</td>
					</tr>
				</table>
				</td>

<!-- end Help and Report Bugs Buttons -->
	<td valign="top" align="right" style="width: 224px;">

