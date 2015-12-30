--------------------------------------------------------------
OPENCLINICA RELEASE NOTES
OpenClinica Version 2.2.1
Document Version 1.0
--------------------------------------------------------------

--------------------------------------------------------------
Updated: Feb 27 2008 Paul Galvin pgalvin@akazaresearch.com

--------------------------------------------------------------

--------------------------------------------------------------
OVERVIEW
--------------------------------------------------------------

This release of OpenClinica addresses issues in OpenClinica 2.2 and upgrading from an existing 2.0.1 installation. Please go to the following page for information on the most important features http://www.openclinica.org/dokuwiki/doku.php?id=ocfeatures:start

After installing the application, you can go here http://www.openclinica.org/dokuwiki/doku.php?id=publicwiki:quickstart and get some information on how to use OpenClinica.

--------------------------------------------------------------
CONTENTS OF THIS DOCUMENT
--------------------------------------------------------------
 
I.   OVERALL PRODUCT FEATURES
II.  ISSUES ADDRESSED AND KNOWN ISSUES
III. SOFTWARE DEPENDENCIES AND SYSTEM REQUIREMENTS
IV.  ABOUT OPENCLINICA
V.   GNU LGPL LICENSE

--------------------------------------------------------------
I. OVERALL PRODUCT FEATURES
--------------------------------------------------------------

Overall Product Features

The main modules include:
* Submit Data: Allows subject enrollment, data submission and validation for use by clinicians and research associates.
* Extract Data: Enables data extraction and filtering of datasets for use by investigators and study directors.
* Manage Study: Facilitates creation and management of studies (protocols), sites, CRFs, users and study event definitions by study directors and coordinators.
* Business Admin/Technical Admin: Allows overall system oversight, auditing, configuration, and reporting by administrators.

Some key features of OpenClinica include: 

* Organization of clinical research by study protocol and site, each with its own set of authorized users, subjects, study event definitions, and CRFs. Support for sharing resources across studies in a secure and transparent manner. 
* Dynamic generation of web-based CRFs for electronic data capture via user-defined clinical parameters and validation logic specified in portable Excel templates.
* Management of longitudinal data for complex and recurring patient visits.
* Data import/export tools for migration of clinical datasets in excel spreadsheets, local databases and legacy data formats.
* Extensive interfaces for data query and retrieval, across subjects, time, and clinical parameters, with dataset export in common statistical analysis formats. 
* Compliance with HIPAA privacy and security guidelines including use of study-specific user roles and privileges, SSL encryption, de-identification of Protected Health Information (PHI), and auditing to monitor access and changes by users.
* A robust and scalable technology infrastructure developed using the Java J2EE framework interoperable with relational databases including PostgreSQL (open source) and Oracle 10G, to support the needs of the clinical research enterprise. 



--------------------------------------------------------------
II. ISSUES ADDRESSED AND KNOWN ISSUES
--------------------------------------------------------------

1. If a user enters data in a group of repeating items that show up in the CRF more than once, data could be lost if user does not mark the CRF complete.  If the user only Saves the CRF, enters data again, and hits Save again, only the first and loast row of the repeated items will be saved.

2. Event status in the extracted data was only showing Available rather than Locked, Stopped, Data Entry Started, or Completed.  The correct status will now be displayed.

3. If the application was set to force a user to change a password the first time they log in and the user logged in as a Business Administrator, the screen would become jumbled.  The only way out of it was to shut down the browswer, open it up and navigate back to OpenClinica.

4. For 2.0.1 users, data would not be able to be extracted in 2.2.  There are three schema patches that will now allow data to be extracted collected prior to 2.2, and the installation docs will tell you which script to run depending on your situation.

Known issues can be found at http://www.openclinica.org/dokuwiki/doku.php?id=ocknownissues:start

--------------------------------------------------------------
III. SOFTWARE DEPENDENCIES AND SYSTEM REQUIREMENTS
--------------------------------------------------------------

Pre-requisites (versions):
    Operating system(s): Windows XP, Windows Server 2003, Redhat Enterprise Linux 4.0+
    Browsers: Internet Explorer 6.0+, Mozilla Firefox 1.5+
    JDK : 1.5.0_14
    Application server : Tomcat 5.5.25
    Database server : PostgreSQL 8.2.5
    OpenClinica version : OpenClinica 2.0.1 or 2.2 

The source code has been removed from the distribution package to make it easier to navigat the file structure.  To access the source code, please follow the instructions at http://www.openclinica.org/dokuwiki/doku.php?id=developerwiki:start#developing_with_openclinica.

NOTE: You must configure the smtp host correctly, or the system will hang when trying to send email notifications. 

--------------------------------------------------------------
IV. ABOUT OPENCLINICA
--------------------------------------------------------------

OpenClinica: Open Source Software Platform for Managing Clinical Research Studies
Professional Open Source Solutions for the Clinical Research Enterprise

OpenClinica is a software platform for protocol configuration, design 
of Case Report Forms (CRFs), and electronic data capture, retrieval, 
and management. It is extensible, modular, standards-based, and open source.

More about OpenClinica: http://www.OpenClinica.org 

Copyright (C) 2005-2007 Akaza Research

Software License

OpenClinica is distributed under the GNU Lesser General Public License (GNU LGPL).

    OpenClinica is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

For details see: http://www.openclinica.org/page.php?pid=61 or LICENSE.txt distributed with this distribution.

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
http://www.akazaresearch.com/
http://www.OpenClinica.org/


--------------------------------------------------------------
V. GNU LGPL LICENSE
--------------------------------------------------------------

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

For the full GNU LGPL License text, see LICENSE.txt included in this package.

