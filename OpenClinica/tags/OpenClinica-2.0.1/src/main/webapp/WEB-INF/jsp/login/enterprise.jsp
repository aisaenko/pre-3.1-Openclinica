<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:include page="../include/home-header.jsp"/>
<jsp:include page="../include/breadcrumb.jsp"/>
<jsp:include page="../include/userbox.jsp"/>
<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		<div class="sidebar_tab_content">
	
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b>Instructions</b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>


<h1>OpenClinica Enterprise</h1>


<P>OpenClinica Enterprise is an enhanced version of OpenClinica, packaged and validated to strict standards of quality, bundled with Akaza's Services and Support.  To find out more about Akaza's Services and Support, you can visit <a href="http://www.openclinica.org/section.php?sid=4">OpenClinica.org</a>.  

<h2>User Support network</h2>

<P><b>OpenClinica Knowledge Base:</b> Access to an on-line portal of help resources, forums and training materials developed by Akaza developers and staff. 

<P><b>Trouble Ticket System:</b> Unlimited online support for all technical/administrative issues that may arise with installation or administration of the system.

<P><b>CRF Exchange:</b> In addition to providing support for proper setup of Clinical Research Forms, Akaza will offer a library of already-generated forms to download and use.  If your CRF needs are specific, Akaza will work with you and utilize its own contacts in the clinical research field to aid in study setup.


<h2>Technical Administration Network</h2>

<P><b>Assurance:</b> We hold an assurance philosophy that includes a commitment to developing OpenClinica under the Lesser Gnu Public License, and are able to work with our clients to address their own intellectual property needs. This client assurance philosophy also extends to GxP, systems validation, and regulatory compliance. Please see  <a href="http://www.openclinica.org/section.php?sid=4">OpenClinica Enterprise 'Services and Support'</a> for more how we can provide you with system assurance, support, and validation services.

<P><b>System Backup:</b> Akaza can assist in back up system properties and files in case of hardware failure, and can easily restore a new working version of OpenClinica with those files intact. Similarly, we can arrange to back up your data from the OpenClinica system and allow for it to be stored in accordance with HIPAA privacy laws, so that you can safely restore the clinical data in case of an emergency.

<P><b>Reporting Services and Tools:</b> Aside from remote monitoring and system administration, Akaza can provide auditing and reporting services through its Enterprise support packages.  We can work with you to create any specific reporting tools that you may require.



<h2>Developer Network</h2>

<P><b>Developer Services and Code:</b> Through our Knowledge Base, Akaza will share code and extended documentation on the OpenClinica system, and can provide support on technical topics through our forums or on a one-on-one basis.

<P><b>Developer Conference Calls:</b>  Akaza offers regular conference calls where we demonstrate the application, all the while answering developers' questions and comments.  These calls are done in a small setting of about eight to ten developers and at least two Akaza staff members present, and are offered monthly.  


<jsp:include page="../include/footer.jsp"/>
