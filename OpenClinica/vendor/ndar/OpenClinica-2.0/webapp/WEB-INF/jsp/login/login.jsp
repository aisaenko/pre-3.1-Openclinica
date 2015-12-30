<%
//if (session.isNew()){
// String referer = request.getHeader("Referer");
// if (referer == null){
//   response.sendRedirect("MainMenu");
// } else {
//   response.sendRedirect(referer);
// }
 //} 
%>
<jsp:include page="../login-include/login-header.jsp"/>

<jsp:include page="../login-include/login-sidebar.jsp"/>
<!-- Main Content Area -->

	<!-- Page Title -->

   <h1>OpenClinica: Managing Clinical Research Studies</h1>   

	<jsp:include page="../login-include/login-alertbox.jsp"/>	

		<p><span class="OpenClinica">OpenClinica</span> is a software platform for protocol configuration, design of Case Report Forms (CRFs), electronic
		data capture, retrieval, and management.  It is extensible, modular, standards-based, and open source.</p>

		<p><span class="OpenClinica">OpenClinica</span> allows:

		<ul>

		<li>Data submission, validation, and annotation by clinicians and research associates</li>

		<li>Data extraction, filtering, and analysis by investigators</li>

		<li>Study management by study directors and study coordinators</li>

		<li>System oversight, auditing, configuration, and reporting by administrators</li>
		</ul>
		
		</p>

 		<p>Login or Request an Account to access your study or site. Based on your role and permissions, you will be able to access selected studies, sites, data, and functionality. 
		</p>
        <br>
		<h2>Actions available <span style="font-size:12px">(upon login based on user role in study)</span></h2>

		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
						<div class="homebox_center">

						<div class="homebox_title"><span class="inactive">Submit Data</span></div><br>
						<div class="homebox_bullets"><span class="inactive">View All Subjects</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Enroll Subject</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Add a new Study Event</span></div><br>

						<div class="homebox_bullets"><span class="inactive">View Study Events</span></div><br>
						
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
						<div class="homebox_center">

						<div class="homebox_title"><span class="inactive">Extract Data</span></div><br>

						<div class="homebox_bullets"><span class="inactive">View Datasets</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Extract Datasets</span></div><br>
						
						<div class="homebox_bullets"><span class="inactive">View Filters</span></div><br>
	
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
				<td class="homeboxes">
	
				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
						<div class="homebox_center">

						<div class="homebox_title"><span class="inactive">Manage Study</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Manage Users</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Manage Subjects</span></div><br>

						<div class="homebox_bullets"><span class="inactive">Manage Sites</span></div><br>
						<div class="homebox_bullets"><span class="inactive">Manage Event Definitions</span></div><br>
						<div class="homebox_bullets"><span class="inactive">Manage CRFs</span></div><br>
						<div class="homebox_bullets"><span class="inactive">View Study Events</span></div><br>
	
						</div>
	
					</div></div></div></div></div></div></div></div>
	
				</td>
			</tr>
		</table>

<!-- End Main Content Area -->
<jsp:include page="../login-include/login-footer.jsp"/>