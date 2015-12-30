OPENCLINICA RELEASE NOTES
OpenClinica Version 2.0.1
Document Version 1.0
--------------------------------------------------------------

Updated: October 28, 2006 jsampson@akazaresearch.com


OpenClinica: Open Source Software Platform for Managing Clinical Research Studies

About OpenClinica

Professional Open Source Solutions for the Clinical Research Enterprise

OpenClinica is a software platform for protocol configuration, design 
of Case Report Forms (CRFs), and electronic data capture, retrieval, 
and management. It is extensible, modular, standards-based, and open source.

More about OpenClinica: http://www.OpenClinica.org 

Software License

OpenClinica is distributed under the GNU Lesser General Public License (GNU LGPL).
For details see: http://www.openclinica.org/license or LICENSE.txt distributed with this distribution.

Developer and Contact Information

Akaza Research, based in Cambridge, MA, provides open informatics solutions
that address the needs of academic and non-profit institutions engaged in 
clinical, healthcare and biomedical research. 

Akaza utilizes internally- and community-developed open source software and 
open standards to provide professional services for the clinical research enterprise. These open solutions enable Akaza's customers to effectively address the challenges 
of data management, compliance, and interoperability in the modern clinical and
healthcare research environment.

Akaza Research
One Kendall Square
Bldg. 400, 4th Floor
Cambridge, MA 02139
phone: 617.621.8585
fax: 617.621.0065
email: contact@akazaresearch.com

For more about Akaza's products and initiatives see: 
http://www.akazaresearch.com
http://www.OpenClinica.org

Software Dependencies

OpenClinica runs on top of any Servlet/JSP container that implements the Servlet 2.4 and JavaServer Pages 2.0 specifications from the Java Community Process. It was developed to run on Apache Jakarta Tomcat 5.5.x.

Currently, OpenClinica also runs on top of the Postgres relational database, but can be modified to work on top of an Oracle relational database.  Using a JDBC (Java Database Connectivity) driver, the database connects to the web application and provides the data to the end-user.

Required:
Jakarta Tomcat 5.5.17
Java 2 SDK 1.5.0.6
JavaServer Pages Standard Tag Library 1.0
Jakarta POI - Java API To Access Microsoft Format Files
Jakarta Digester - XML to Java Object Configuration 
Postgres 8.1.4
Postgres JDBC Driver Version 8.0-310 JDBC 3


Compatibility and System Requirements

Apache Jakarta Tomcat Servlet/JSP container. Tomcat version 5 implements the Servlet 2.4 and JavaServer Pages 2.0 specifications from the Java Community Process, and includes many additional features that make it a useful platform for developing and deploying web applications and web services.

System Requirements

Compatible Server Operating Systems
	Redhat Enterprise Linux 4.0+
	Redhat Linux 8.0+

Client Browser/OS Requirements
	Internet Explorer 5.5+
	Mozilla Firefox 1.5+

Hardware Requirements
	Memory: 256 MB minimum (1 GB Recommended)
	Disk Space: 500 MB minimum (1 GB Recommended)

New Features in the 2.0 Release:


Integration with Enterprise/Organization wide systems
-----------------------------------------------------
* Enable User authentication from a remote directory server using the LDAP protocol. 

Core Product Features
---------------------
* Reduce the number of clicks a user has to do to get to entering/accessing data. 
* Allow Subject record to be added in a single step as path of the View/Manage Subjects Page.
* Added A view subjects dashboard to streamline review of subject tracking and direct access to subject/event management and EDC functionality
* CRF Interview Name and Date can be set as optional or required fields (in study creation)
* CRF Interview Name and Date can be set to default to blank or to be pre-populated with user's name and the date of the study event (in study creation)
* Allow configuration of study options and properties in system properties.
* Provide study configuration option to auto-generate study subject id at enroll.
* Some entities should have varying levels of status other than just on and off, since there are multiple levels of management that different protocols go through.
  "locked", "disabled", "pending", "suspended", "revised", "enabled"
* Account/password expirations with configuration in system properties.
* Allow printing of completed/in-progress CRFs with a single click. New Header information added to CRFs.

Data Output and Auditing
------------------------
* Provide data output in CDISC ODM XML format.
* Provide data output in SAS format
* Improved audit logging and audit reporting

Performance Enhancements
------------------------
* Improved Database Performance

Product Support 
---------------
* Added a link between OpenClinica Implementation and OpenClinica Enterprise Service Infrastructure for submitting trouble tickets. 
* Provide well-documented and robust configuration and build options for OpenClinica.



- A number of bug fixes have gone into this release, and we have included documentation on those fixes in PATCH.1.1.txt.  The fixes include:

* Data warehouse/Export Data issues have been resolved.

Overall Product Features

The main modules include:
1. Submit Data: Allows subject enrollment, data submission and validation for use by clinicians and research associates.
2. Extract Data: Enables data extraction and filtering of datasets for use by investigators and study directors.
3. Manage Study: Facilitates creation and management of studies (protocols), sites, CRFs, users and study event definitions by study directors and coordinators.
4. Business Admin/Technical Admin: Allows overall system oversight, auditing, configuration, and reporting by administrators.

Some key features of OpenClinica include: 

* Organization of clinical research by study protocol and site, each with its own set of authorized users, subjects, study event definitions, and CRFs. Support for sharing resources across studies in a secure and transparent manner. 
* Dynamic generation of web-based CRFs for electronic data capture via user-defined clinical parameters and validation logic specified in portable Excel templates.
* Management of longitudinal data for complex and recurring patient visits.
* Data import/export tools for migration of clinical datasets in excel spreadsheets, local databases and legacy data formats.
* Extensive interfaces for data query and retrieval, across subjects, time, and clinical parameters, with dataset export in common statistical analysis formats. 
* Compliance with HIPAA privacy and security guidelines including use of study-specific user roles and privileges, SSL encryption, de-identification of Protected Health 
 Information (PHI), and auditing to monitor access and changes by users.
* A robust and scalable technology infrastructure developed using the Java J2EE framework interoperable with relational databases including PostgreSQL (open source) and Oracle 10G, to support the needs of the clinical research enterprise. 

More details on OpenClinica: http://www.OpenClinica.org 
 
 
GNU LGPL Creative Commons License text

OpenClinica is distributed under the GNU Lesser General Public License (GNU LGPL), 
summarized in the Creative Commons text here:

http://creativecommons.org/licenses/LGPL/2.1/

The GNU Lesser General Public License is a Free Software license. Like any Free Software
license, it grants to you the four following freedoms:

0. The freedom to run the program for any purpose.
1. The freedom to study how the program works and adapt it to your needs.
2. The freedom to redistribute copies so you can help your neighbor.
3. The freedom to improve the program and release your improvements to the public, so 
that the whole community benefits.

You may exercise the freedoms specified here provided that you comply with the express conditions of this license. The LGPL is intended for software libraries, rather than executable programs.

The principal conditions are:

* You must conspicuously and appropriately publish on each copy distributed an appropriate copyright notice and disclaimer of warranty and keep intact all the notices that refer to this License and to the absence of any warranty; and give any other recipients of the Program a copy of the GNU Lesser General Public License along with the Program. Any translation of the GNU Lesser General Public License must be accompanied by the GNU Lesser General Public License.
* If you modify your copy or copies of the library or any portion of it, you may distribute the resulting library provided you do so under the GNU Lesser General Public License. However, programs that link to the library may be licensed under terms of your choice, so long as the library itself can be changed. Any translation of the GNU Lesser General Public License must be accompanied by the GNU Lesser General Public License.
* If you copy or distribute the library, you must accompany it with the complete corresponding machine-readable source code or with a written offer, valid for at least three years, to furnish the complete corresponding machine-readable source code. You need not provide source code to programs which link to the library.

Any of these conditions can be waived if you get permission from the copyright holder.
Your fair use and other rights are in no way affected by the above.

Full GNU LGPL License text:
http://www.gnu.org/copyleft/lesser.html
