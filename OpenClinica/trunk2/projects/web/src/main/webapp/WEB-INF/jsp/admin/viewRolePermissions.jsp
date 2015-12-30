<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>

<script type="text/JavaScript" language="JavaScript" src="../../includes/dataTable/jquery.js"></script>

<script type="text/JavaScript" language="JavaScript" src="../../includes/dataTable/comboFilter.js"></script>
<script type="text/JavaScript" language="JavaScript" src="../../includes/dataTable/jquery.dataTables.js"></script>
<script type="text/JavaScript" language="JavaScript" src="../../includes/dataTable/four_buttons_paggination.js"></script>

<!-- <script type="text/JavaScript" language="JavaScript" src="includes/dataTable/ColVis.js"></script> -->

<link rel="stylesheet" href="../../includes/dataTable/dataTable.css" type="text/css"/>


<jsp:include page="../include/managestudy_top_pages_new.jsp"/>

<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr><jsp:include page="../include/sideInfo.jsp"/>

<h1><span class="title_manage">
<fmt:message key="view_user_permissions" bundle="${resword}"/>
</span></h1>
<div class="homebox_bullets"><a href="pages/admin/viewRolePermissions">Edit User Permissions</a></div>
<script type="text/javascript" charset="utf-8">
//jQuery.noConflict(); // start substituting $ for jQuery

jQuery(document).ready( function () {
	jQuery(".combo_filter td").each( function ( i ) {
		jQuery('select', this).change( function () {
			thisTable.fnFilter( jQuery(this).val(), i );
		} );
	} );
				
				 jQuery(".search_init").focus(function () {
		            this.className = "";
		            this.value = "";
		        });

		        jQuery(".search_init").blur(function (i) {
		            if (this.value == "") {
		                this.className = "search_init";
		                this.value = this.initVal;
		            }
		        });

		       
				jQuery(this).find(".search_init").each(function (i) {
					this.visibleIndex = i;
					this.initVal = this.value;
				});
		        var thisTable = jQuery('#view_role_permission').dataTable({
		            	//"sDom": 'C<"clear">lfrtip',
		                 "sDom": 'plfrti',
			            "sScrollXInner": "150%",
						"iDisplayLength": 15,
						"aLengthMenu": [[15, 25, 50, -1], [15, 25, 50, "All"]],
						"sPaginationType": "four_button",
		              	"oLanguage": {
		              		 //"sSearch": "Search all columns:"
							"sUrl": "../../includes/dataTable/internalization/<c:out value="${dataTableLanguageFile}" />"
		                }
		            });
		            jQuery(this).find(".search_init").keyup(function () {
		                var visIndex = typeof this.visibleIndex == 'undefined' ? 0 : this.visibleIndex;

		                /* Filter on the column (the index) of this element */
		                thisTable.fnFilter(this.value, visIndex);
		            });
					
					jQuery(window).bind('resize', function () {
					   thisTable.fnAdjustColumnSizing();
					} );
		       
		     	} );

</script>
<div style="border: 1px solid grey; padding: 2px">
<table cellpadding="0" cellspacing="0" border="0" class="dataTable" id="view_role_permission">

<thead>
<tr >
<c:forEach items="${vrpTableHeader}" var="column_title" varStatus="ii" begin="0" step="1">
	<th ><c:out value="${column_title}" />   </th>
</c:forEach>	
</tr>
<tr  class="combo_filter">
<!-- filters combo-->
<td><input type="text" name="search_url" value=" " class="search_init" /></td>
<c:forEach items="${vrpTableHeader}" var="column_title" varStatus="ii" begin="1" step="1">
	<td style="text-align:center; width:5px;"><select style="width:  100%"><option value=""></option>
	<option value="Yes"><fmt:message key="yes_permission" bundle="${resword}"/></option>
	<option value="No"><fmt:message key="no_permission" bundle="${resword}"/></option></select></td>
</c:forEach>
</tr>
</thead>


<tbody>
<c:set var="yes_val"><fmt:message key="yes_permission" bundle="${resword}"/> </c:set>
<c:set var="no_val"><fmt:message key="no_permission" bundle="${resword}"/> </c:set>
<c:forEach items="${vrpTableContent}" var="row"  begin="0" step="1">
<tr>
<c:forEach items="${row}" var="field" varStatus="rowLoop" begin="0" step="1">

	
	<c:choose>
	
<c:when test="${field== yes_val}">
<td align="center">
<img hspace='2' border='0'  title="Permitted" alt='Permitted' src="../../images/icon_DEcomplete.gif" value="Yes"/>
	</td>
</c:when>
<c:when test="${field== no_val}">
<td align="center" style="visibility:hidden">
No
</td>
</c:when>
<c:otherwise>
<td align="left">
	${row[rowLoop.index]}
	</td>
</c:otherwise>
</c:choose>

</c:forEach>
</tr>
</c:forEach>
</tbody>
</table>
</div>
<br>
<form id="fr_cancel_button" method="get">
<input type="button" onclick="confirmExitAction('ListUserAccounts', '${pageContext.request.contextPath}');"  name="exit" value="<fmt:message key="exit" bundle="${resword}"/>   " class="button_medium"/>
</form>


<jsp:include page="../include/footer.jsp">
 <jsp:param name="isSpringControllerFooter" value="2" />
    </jsp:include>
</body>
</html>