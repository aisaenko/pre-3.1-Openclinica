<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />

<jsp:include page="include/home-header.jsp"/>
<jsp:include page="include/breadcrumb.jsp"/>
<jsp:include page="include/userbox.jsp"/>
<jsp:include page="include/sidebar.jsp"/>

<h1>OpenClinica: An error has occurred</h1>

<font class="bodytext">
<P>An error has occurred in the application.  This can be for a number of reasons, not
all of them might have to do with the actual Web application.  If there are any messages
above this text, please cut and paste them into an email to
<a href="mailto:openclinica-admin@akazaresearch.com">Akaza Research</a>.
<P>If there are no messages, you might try clicking on the links in the header, and see what happens.  This might
 reinitialize your connection with the database. Or <a href="<%=request.getHeader("Referer")%>">click here</a> to take you back where you came from.
<P>In any event, if you can't get back to a working page, please let us know.  Please be sure to include
<ul><li>what you were doing,
<li>when it happened,
<li>and any other details (like error messages) that might be important.
</ul>
<P>Thanks,
<P>The OpenClinica Development Team
</font>

<jsp:include page="include/footer.jsp"/>
