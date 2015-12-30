
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function layersShowOrHide() {
    var arrayArgs = layersShowOrHide.arguments;
    var objLayer;
    var strShowOrHide = arrayArgs[0];
    var i;

    for (i=1;i<=arrayArgs.length-1;i++) {
        if ((objLayer=MM_findObj(arrayArgs[i]))!=null) {
	    // for IE and NS compatibility
            if (objLayer.style) { objLayer = objLayer.style; }
            objLayer.visibility = strShowOrHide;
        }
    }
}

function setImage(strImageName, strImageFullPath) {
    var objImage;

    objImage = MM_findObj(strImageName);
    if (objImage != null) { objImage.src = strImageFullPath; }
}

function popUp(strFileName, strTarget) {
	window.open(strFileName, strTarget, 'menubar=yes,toolbar=no,scrollbars=yes,resizable,width=700,height=450,screenX=0,screenY=0');
}

function newImage(arg) {
	if (document.images) {
		rslt = new Image();
		rslt.src = arg;
		return rslt;
	}
}

function changeImages() {
	if (document.images && (preloadFlag == true)) {
		for (var i=0; i<changeImages.arguments.length; i+=2) {
			document[changeImages.arguments[i]].src = changeImages.arguments[i+1];
		}
	}
}
var preloadFlag = false;
function preloadImages() {
	if (document.images) {
		bt_GO_h = newImage("/images/bt_GO_d.gif");
		preloadFlag = true;

	}
}

var isDOM = (document.getElementById ? true : false);
var isIE4 = ((document.all && !isDOM) ? true : false);
var isNS4 = (document.layers ? true : false);
function getRef(id) {
if (isDOM) return document.getElementById(id);
if (isIE4) return document.all[id];
if (isNS4) return document.layers[id];
}
function getSty(id) {
return (isNS4 ? getRef(id) : getRef(id).style);
}



function MM_jumpMenu(targ,selObj,restore){ //v3.0
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
}


/* Specifies the period of time between updates:
    month - once a month
    date - once per every day of the month (repeats the next month)
    weekday - once per every day of the week (repeats the next week)
    hour - once per hour (repeats the next day)
    request - once per browser request (default)
*/

var updatePeriods = new Array("month","date","weekday","hour","request")

// Invoked to display rotated HTML content in a Web page. The period
// argument should be an element of the updatePeriods array.

function displayRotatedContent(period) {
 var updatePeriod = -1
 for(var i=0;i<content.length;++i) {
  if(period.toLowerCase() == updatePeriods[i].toLowerCase()) {
   updatePeriod = i
   break
  }
 }
 var s = selectHTML(updatePeriod)
 document.write(s)
}

function selectHTML(updatePeriod) {
 var n = 0
 var max = content.length
 var d = new Date()
 switch(updatePeriod) {
  case 0: // Month (0 - 11)
   n = d.getMonth()
   break
  case 1: // Date (1 - 31 scaled to 0 - 30)
   n = d.getDate() - 1
   break
  case 2: // Weekday (0 - 6)
   n = d.getDay()
   break
  case 3: // Hour (0 - 23)
   n = d.getHours()
   break
  case 4: // Request (Default)
  default:
   n = selectRandom(max)
 }
 n %= max
 return content[n]
}

// Select a random integer that is between 0 (inclusive) and max (exclusive)
function selectRandom(max) {
 var r = Math.random()
 r *= max
 r = parseInt(r)
 if(isNaN(r)) r = 0
 else r %= max
 return r
}

//parts added 12-18-03, tbh
function confirmSaveAndContinue () {

	var yesno = confirm("Your data will now be saved to the database. This may take a minute or two; \nplease be patient and do not attempt to reload or make changes to the page. \nClick 'OK' to continue or 'Cancel' to return to the page without saving.","");
	//if (yesno == true) { alert ("OK was chosen"); } else { alert("Cancel was chosen"); }
	return yesno;
}

function disableAllButtons (theform) {

	if (document.all || document.getElementById) {
		for (i = 0; i < theform.length; i++) {
			var tempobj = theform.elements[i];
			if (tempobj.type.toLowerCase() == "submit" || tempobj.type.toLowerCase() == "reset") {
				tempobj.disabled = true;
			}
		}
	}

	//alert ("function has ended. getting ready to return");

	return true;
}

function submitFormDataConfirm (theform) {

	if (confirmSaveAndContinue()) {
		return disableAllButtons(theform);
	} else {
		return false;
	}
}

function submitFormReportCheck (theformlist) {
	var number = 0;
	for (i = 0; i < theformlist.length; i++) {
		if (theformlist[i].selected) number++;
	}
	//if (isNaN(number)) number = 0;
	if (number > 50) {
		alert("You are only allowed to choose up to a maximum of fifty (50) variables.  You have picked "+number+".  Please go back to the form and remove some of your selections.  For Data Dumps of more than 50 variables, please contact your Project Administrator or DBA.");
                return false;
	} else {
		return true;
	}
}


//---------------------added by jxu,10-15-2004------------------------

//-------------------------------------------------------------------------
// Function: setfocus
//
// Set the focus to the first form element.
//-------------------------------------------------------------------------

function setFocus() {

    var finished = false;
    var index = 0;
    if (document.forms[0] != null)
    {       
      while ( finished == false ) 
      {
        if (document.forms[0].elements[index].type != 'hidden') 
        {
            document.forms[0].elements[index].focus();
            finished = true; 
        }

        index++;
      }
    }
}

//----------------------------------------------------
function trimString (str) {
  str = this != window? this : str;
  return str.replace(/^\s+/g, '').replace(/\s+$/g, '');
}


//-------------------------------------------------------------------------
// Function: getQueryVariable
//
// returns the value of a key/value pair from the page's URL 'GET' parameters
//-------------------------------------------------------------------------

function getQueryVariable(variable) {
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
    if (pair[0] == variable) {
      return unescape(pair[1]);
    }
  } 
  return '';
}

//-------------------------------------------------------------------------
// Function: getName
//
// returns the 'id' attribute of a DOM element by its ID
//-------------------------------------------------------------------------

function getName(spanId) {

	obj = getRef(spanId);
	str = obj.getAttribute('id');
	str = trimString(str);
	return str;

}

//-------------------------------------------------------------------------
// Function: getContent
//
// returns the html/text content of a DOM element by its ID
//-------------------------------------------------------------------------

function getContent(spanId) {
	obj = getRef(spanId);
	str = obj.innerHTML;
	return str;
}

//-------------------------------------------------------------------------
// Function: openDNoteWindow
//
// Pops up a new browser window for discrepancy notes, including the validation error message text if applicable
//-------------------------------------------------------------------------

function openDNoteWindow(inURL, spanID) {
	
	if (spanID) {
		strErrMsg = getContent(spanID);
		// add the error message to the URL
		// encode it using 'escape'
		if (inURL.match(/\?/)) {
			if (inURL.match(/\?$/)) {
				newURL = inURL + 'strErrMsg=' + escape(strErrMsg);
			} else {
				newURL = inURL + '&strErrMsg=' + escape(strErrMsg);
			}
		} else {
			newURL = inURL + '?strErrMsg=' + escape(strErrMsg);
		}
	} else {
		newURL = inURL;
	}
    openNewWindow(newURL, 
           'dnote_win',
           'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes', 
           'dnote');

}
//--------------------------------------
//--pop up a window which is smaller 
//------------------------------------------
function openDSNoteWindow(inURL, spanID) {
	
	if (spanID) {
		strErrMsg = getContent(spanID);
		// add the error message to the URL
		// encode it using 'escape'
		if (inURL.match(/\?/)) {
			if (inURL.match(/\?$/)) {
				newURL = inURL + 'strErrMsg=' + escape(strErrMsg);
			} else {
				newURL = inURL + '&strErrMsg=' + escape(strErrMsg);
			}
		} else {
			newURL = inURL + '?strErrMsg=' + escape(strErrMsg);
		}
	} else {
		newURL = inURL;
	}
    openNewWindow(newURL, 
           'dnote_win',
           'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes', 
           'dsnote');

}


function openVNoteWindow(inURL) {

    openNewWindow(inURL, 
           'def_win',
           'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes', 
           'dnote');

}


//-------------------------------------------------------------------------
// Function: openNewWindow
//
// Pops up a new browser window containing the definitions page, and scrolls
//     to the correct spot
//-------------------------------------------------------------------------

function openDefWindow(inURL) {

    openNewWindow(inURL, 
           'def_win',
           'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes', 
           'small');

}

//-------------------------------------------------------------------------
// Function: openNctEntryWindow
//
// Pops up a new browser window containing the NCT Entry screen
//-------------------------------------------------------------------------

function openNctEntryWindow(inURL) {

    openNewWindow(inURL, 
           '_blank',
           'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes', 
           'medium');

}

//-------------------------------------------------------------------------
// Function: openDocWindow
//
// Pops up a new browser window containing a document, such as the 
// PRS Reference Guide.
//-------------------------------------------------------------------------

function openDocWindow(inURL) {

    openNewWindow(inURL, 
           '_blank',
           'directories=no,location=no,menubar=yes,scrollbars=yes,toolbar=no,status=no,resizable=yes', 
           'medium');

}

//-------------------------------------------------------------------------
// Function: openNewWindow
//
// Pops up a new browser window containing the definitions page, and scrolls
//     to the correct spot
//-------------------------------------------------------------------------

function openNewWindow(inURL, name, features, windowSize) {

    // Add check for browser capability
    var old_browser = true;
    if (window.screen != null) old_browser = false;

    if (features == "") {
        features = "toolbar=yes,directories=yes,location=1,status=yes,menubar=yes,scrollbars=yes,resizable=yes";
    }
    
    var height=250;
    var width=350;
    var screenHeight = 480;
    var screenWidth = 640;

   if(windowSize == 'small')
   {
       height = 150;
       width = 200;
   }
   if(windowSize == 'medium')
   {
       height = 300;
       width = 500;
   }
   if(windowSize == 'dnote')
   {
       height = 250;
       width = 200;
   }
   if(windowSize == 'dsnote')
   {
       height = 200;
       width = 200;
   }



    if (window.screen != null)
    {
        screenHeight = window.screen.height;
        screenWidth = window.screen.width;
    }

    if (screenWidth > 640)
    {
        width = width + (screenWidth - 640)*.50;
    }

    if(screenHeight > 480)
    {
        height = height + (screenHeight - 480)*.50;
    }

    features += ",width=" + width + ",height=" + height;

    var docView = window.open (inURL, name, features);

    docView.focus();


}


//-------------------------------------------------------------------------
// Function: MM_findObjInParentWin
//
// Finds the specified object in the parent window if it exists
//     Must be called from within a popup window opened by a parent window
//-------------------------------------------------------------------------

function MM_findObjInParentWin(strParentWinImageName) { //v4.0
    var objImage;

	if (window.opener && !window.opener.closed) {
		objImage = MM_findObj(strParentWinImageName, window.opener.document);
	}

    return objImage;
}

//-------------------------------------------------------------------------
// Function: setImageInParentWin
//
// Sets/changes the source file of an image in a parent window
//     Must be called from within a popup window that was opened by the parent window
//-------------------------------------------------------------------------

function setImageInParentWin(strParentWinImageName, strParentWinImageFullPath) {
    var objImage;

	if (window.opener && !window.opener.closed) {

	    objImage = MM_findObjInParentWin(strParentWinImageName);
		if (objImage != null) { objImage.src = strParentWinImageFullPath; }

	}
}


// new functions for revised View Subjects screen 6-12-06

function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function setImage(strImageName, strImageFullPath) {
    var objImage;

    objImage = MM_findObj(strImageName);
    if (objImage != null) { objImage.src = strImageFullPath; }
}

function leftnavExpand(strLeftNavRowElementName){

	var objLeftNavRowElement;

    objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
    if (objLeftNavRowElement != null) {
        if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; } 
	    objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";		
	}
}






function layersShowOrHide() {
    var arrayArgs = layersShowOrHide.arguments;
    var objLayer;
    var strShowOrHide = arrayArgs[0];
    var i;
	
    for (i=1;i<=arrayArgs.length-1;i++) {
        if ((objLayer=MM_findObj(arrayArgs[i]))!=null) {
	    // for IE and NS compatibility
            if (objLayer.style) { objLayer = objLayer.style; } 
            objLayer.visibility = strShowOrHide;            
        } 
    }
}


/* 
Functions that swaps images.  These functions were generated by Dreamweaver, but are
not used by e-guana.
*/
function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

var isDOM = (document.getElementById ? true : false); 
var isIE4 = ((document.all && !isDOM) ? true : false);
var isNS4 = (document.layers ? true : false);
function getRef(id) {
if (isDOM) return document.getElementById(id);
if (isIE4) return document.all[id];
if (isNS4) return document.layers[id];
}
function getSty(id) {
return (isNS4 ? getRef(id) : getRef(id).style);
} 





function gotopage(){



	if(document.jumpform.category.options[document.jumpform.category.selectedIndex].value != ""){



		document.location.href = document.jumpform.category.options[document.jumpform.category.selectedIndex].value;}



}




// new functions for View Subjects status menus 9-13-06

function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function setImage(strImageName, strImageFullPath) {
    var objImage;

    objImage = MM_findObj(strImageName);
    if (objImage != null) { objImage.src = strImageFullPath; }
}

function leftnavExpand(strLeftNavRowElementName){

	var objLeftNavRowElement;

    objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
    if (objLeftNavRowElement != null) {
        if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; } 
	    objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";		
	}
}






function layersShowOrHide() {
    var arrayArgs = layersShowOrHide.arguments;
    var objLayer;
    var strShowOrHide = arrayArgs[0];
    var i;
	
    for (i=1;i<=arrayArgs.length-1;i++) {
        if ((objLayer=MM_findObj(arrayArgs[i]))!=null) {
	    // for IE and NS compatibility
            if (objLayer.style) { objLayer = objLayer.style; } 
            objLayer.visibility = strShowOrHide;            
        } 
    }
}


/* 
Functions that swaps images.  These functions were generated by Dreamweaver, but are
not used by e-guana.
*/
function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

var isDOM = (document.getElementById ? true : false); 
var isIE4 = ((document.all && !isDOM) ? true : false);
var isNS4 = (document.layers ? true : false);
function getRef(id) {
if (isDOM) return document.getElementById(id);
if (isIE4) return document.all[id];
if (isNS4) return document.layers[id];
}
function getSty(id) {
return (isNS4 ? getRef(id) : getRef(id).style);
} 





function gotopage(){



	if(document.jumpform.category.options[document.jumpform.category.selectedIndex].value != ""){



		document.location.href = document.jumpform.category.options[document.jumpform.category.selectedIndex].value;}



}


// new functions for revised View Subjects screen 9-13-06


function getObject( obj ) {

  // step 1
  if ( document.getElementById ) {
    obj = document.getElementById( obj );

  // step 2
  } else if ( document.all ) {
    obj = document.all.item( obj );

  //step 3
  } else {
    obj = null;
  }

  //step 4
  return obj;
  }

function LockObject( obj, e ) {

  // step 1
  var tempX = 0;
  var tempY = 0;
  var offsetx = -17;
  var offsety = -15;
  var objHolder = obj;

  // step 2
  obj = getObject( obj );
  if (obj==null) return;

  // step 3
  if (document.all) {
    tempX = event.clientX + document.body.scrollLeft;
    tempY = event.clientY + document.body.scrollTop;
  } else {
    tempX = e.pageX;
    tempY = e.pageY;
  }

  // step 4
  if (tempX < 0){tempX = 0}
  if (tempY < 0){tempY = 0}

  // step 5
  obj.style.top  = (tempY + offsety) + 'px';
  obj.style.left = (tempX + offsetx) + 'px';

  // step 6
  displayObject( objHolder, true );
  }



function moveObject( obj, e ) {

  // step 1
  var tempX = 0;
  var tempY = 0;
  var offsetx = -2;
  var offsety = 10;
  var objHolder = obj;

  // step 2
  obj = getObject( obj );
  if (obj==null) return;

  // step 3
  if (document.all) {
    tempX = event.clientX + document.body.scrollLeft;
    tempY = event.clientY + document.body.scrollTop;
  } else {
    tempX = e.pageX;
    tempY = e.pageY;
  }

  // step 4
  if (tempX < 0){tempX = 0}
  if (tempY < 0){tempY = 0}

  // step 5
  obj.style.top  = (tempY + offsety) + 'px';
  obj.style.left = (tempX + offsetx) + 'px';

  // step 6
  displayObject( objHolder, true );
  }



function displayObject( obj, show ) {

  // step 1
  obj = getObject( obj );
  if (obj==null) return;

  // step 2
  obj.style.display = show ? 'block' : 'none';
  obj.style.visibility = show ? 'visible' : 'hidden';
  }





