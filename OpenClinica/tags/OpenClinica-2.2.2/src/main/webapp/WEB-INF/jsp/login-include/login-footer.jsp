
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>


<!-- END MAIN CONTENT AREA -->
</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td valign="bottom">

<!-- Footer -->

		<table border="0" cellpadding=0" cellspacing="0" width="100%">
			<tr>
				<td class="footer">
				<!--<a href="#">About OpenClinica</a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="#">Terms of Use</a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="#">Privacy Policy</a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				-->
				<a href="http://www.openclinica.org" target="new"><fmt:message key="openclinica_portal" bundle="${resword}"/></a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="javascript:openDocWindow('help/index.html')"><fmt:message key="help" bundle="${resword}"/></a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="javascript:reportBug()"><fmt:message key="openclinica_feedback" bundle="${resword}"/></a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;				
				<a href="Contact"><fmt:message key="contact" bundle="${resword}"/></a>				
				</td>
				<td class="footer" align="right"><fmt:message key="Version_release" bundle="${resword}"/>&nbsp;&nbsp;</td>
				<td align="right" valign="bottom"><a href="http://www.akazaresearch.com"><img src="images/Akazalogo.gif" border="0" alt="<fmt:message key="developed_by_akaza" bundle="${resword}"/>" title="<fmt:message key="developed_by_akaza" bundle="${resword}"/>"></a></td>
			</tr>
		</table>

<!-- End Footer -->

		</td>
	</tr>
</table>
		

</body>

</html>
