addEvent(window, "load", sortables_init);
addEvent(window, "load", highlightSentPkg);



var SORT_COLUMN_INDEX;

function sortables_init() {
    if (!document.getElementsByTagName) return;
    tbls = document.getElementsByTagName("table");
    for (ti=0;ti<tbls.length;ti++) {
        thisTbl = tbls[ti];
        if (((' '+thisTbl.className+' ').indexOf("sortable") != -1) && (thisTbl.id)) {
            //initTable(thisTbl.id);
            ts_makeSortable(thisTbl);
            
            
        }
    }
}

function ts_makeSortable(table) {
    if (table.rows && table.rows.length > 0) {
        var firstRow = table.rows[0];
    }
    if (!firstRow) return;
    
    
    for (var i=0;i<firstRow.cells.length;i++) {
    
    	if( i == 0 ) continue;
        var cell = firstRow.cells[i];
        var txt = ts_getInnerText(cell);
      
        if ( i != 1 )
        {
            
       		cell.innerHTML = '<td><a href="#" class="sortheader" onclick="ts_resortTable(this);return false;">'+'<span class="sortarrow"><img src="images/edesign_updown.gif" border="0"></span>'+txt+'</a></td>';
       	} else {
       	   
       		cell.innerHTML = '<td><a href="#" class="sortheader" onclick="ts_resortTable(this);return false;">'+'<span class="sortarrow"><img src="images/edesign_down.gif" border="0"></span>'+txt+'</a></td>';
       	}
       
    
    }
}

function ts_getInnerText(el) {
	if (typeof el == "string") return el;
	if (typeof el == "undefined") { return el };
	if (el.innerText) return el.innerText;	//Not needed but it is faster
	var str = "";
	
	var cs = el.childNodes;
	var l = cs.length;
	for (var i = 0; i < l; i++) {
		switch (cs[i].nodeType) {
			case 1: //ELEMENT_NODE
				str += ts_getInnerText(cs[i]);
				break;
			case 3:	//TEXT_NODE
				str += cs[i].nodeValue;
				break;
		}
	}
	return str;
}

function ts_resortTable(lnk) {  
    var span;
    for (var ci=0;ci<lnk.childNodes.length;ci++) {
        if (lnk.childNodes[ci].tagName && lnk.childNodes[ci].tagName.toLowerCase() == 'span') span = lnk.childNodes[ci];
    }
    var spantext = ts_getInnerText(span);
    var td = lnk.parentNode;
    
    // hmmm, IE has TH as next item, firefox has TD ... odd
    var dbg = lnk;
    while(dbg) {
       if (dbg.tagName == "TH") {
         td = dbg;
         break;
       }
       dbg = dbg.parentNode;
    }    
    var column = td.cellIndex;
    var table = getParent(td,'TABLE');
    
    // Work out a type for the column
    if (table.rows.length <= 1) return;
    var itm = normalizeString(ts_getInnerText(table.rows[1].cells[column]));
    sortfn = ts_sort_caseinsensitive;
    if (itm.match(/^\d\d[\/-]\d\d[\/-]\d\d\d\d$/)) sortfn = ts_sort_date;
    if (itm.match(/^\d\d[\/-]\d\d[\/-]\d\d$/)) sortfn = ts_sort_date;
    if (itm.match(/^[\d\.]+$/)) sortfn = ts_sort_numeric;
    if (itm.match(/[KMG]B$/)) sortfn = ts_sort_size;
    
    SORT_COLUMN_INDEX = column;
    var firstRow = new Array();
    var newRows = new Array();
    for (i=0;i<table.rows[0].length;i++) { firstRow[i] = table.rows[0][i]; }
    for (j=1;j<table.rows.length;j++) { newRows[j-1] = table.rows[j]; }

    newRows.sort(sortfn);

    if (span.getAttribute("sortdir") == 'down') {
       
        ARROW = '<td><img src="images/edesign_up.gif" border="0"></td>';
        
        span.setAttribute('sortdir','up');
    } else {
      newRows.reverse();
        ARROW = '<td><img src="images/edesign_down.gif" border="0"></td>';
        span.setAttribute('sortdir','down');
    }
  

    for (i=0;i<newRows.length;i++) { if (!newRows[i].className || (newRows[i].className && (newRows[i].className.indexOf('sortbottom') == -1))) table.tBodies[0].appendChild(newRows[i]);}
    
    for (i=0;i<newRows.length;i++) { if (newRows[i].className && (newRows[i].className.indexOf('sortbottom') != -1)) table.tBodies[0].appendChild(newRows[i]);}
    
    
    var allspans = document.getElementsByTagName("span");
    for (var ci=0;ci<allspans.length;ci++) {
        if (allspans[ci].className == 'sortarrow') {
            if (getParent(allspans[ci],"table") == getParent(lnk,"table")) { // in the same table as us?
              
                allspans[ci].innerHTML = '<td><img src="images/edesign_updown.gif" border="0"></td>';
            }
        }
    }
        
    span.innerHTML = ARROW;
}

function getParent(el, pTagName) {
	if (el == null) return null;
	else if (el.nodeType == 1 && el.tagName.toLowerCase() == pTagName.toLowerCase())	// Gecko bug, supposed to be uppercase
		return el;
	else
		return getParent(el.parentNode, pTagName);
}



function ts_sort_date(a,b) { 
    aa = normalizeString(ts_getInnerText(a.cells[SORT_COLUMN_INDEX]));
    bb = normalizeString(ts_getInnerText(b.cells[SORT_COLUMN_INDEX]));
   
    dd1=aa.substr(3,2);
	dd2=bb.substr(3,2);
	mm1=aa.substr(0,2);
	mm2=bb.substr(0,2);
	var yy1;
	var yy2;
	
	if (aa.length == 10) {
           yy1=aa.substr(6,4);
	     
    } else {
        yr = aa.substr(6,2);
        if (parseInt(yr) < 50) { yr = '20'+yr; } else { yr = '19'+yr; }

        yy1=yr+aa.substr(3,2);
	
    }
    if (bb.length == 10) {
        
		 yy2=bb.substr(6,4);
	
    } else {
        yr = bb.substr(6,2);
        if (parseInt(yr) < 50) { yr = '20'+yr; } else { yr = '19'+yr; }
		 yy2=yr+bb.substr(3,2);
	
    }

if(yy1==yy2)
{

	if(mm1==mm2)
	{
	 
		if(dd1==dd2)
		{
			
			return 0;
		}
		if(dd1<dd2)
		{
			
		return  -1;
		}
		if(dd1>dd2)
		{
		
		return 1;
		}
	}
	if(mm1<mm2){	return -1;}
    if(mm1>mm2){ return 1;}

}
if(yy1<yy2){	return -1;}
if(yy1>yy2){ return 1;}
}


function ts_sort_numeric(a,b) { 
    aa = parseFloat(ts_getInnerText(a.cells[SORT_COLUMN_INDEX]));
    if (isNaN(aa)) aa = 0;
    bb = parseFloat(ts_getInnerText(b.cells[SORT_COLUMN_INDEX])); 
    if (isNaN(bb)) bb = 0;
    return aa-bb;
}

function ts_sort_caseinsensitive(a,b) {
    aa = ts_getInnerText(a.cells[SORT_COLUMN_INDEX]).toLowerCase();
    bb = ts_getInnerText(b.cells[SORT_COLUMN_INDEX]).toLowerCase();
    if (aa==bb) return 0;
    if (aa<bb) return -1;
    return 1;
}

function ts_sort_size(a,b) {
   
   
    aa = ts_getInnerText(a.cells[SORT_COLUMN_INDEX]).toUpperCase();
    bb = ts_getInnerText(b.cells[SORT_COLUMN_INDEX]).toUpperCase();
  
    
    cc=convertToActualSize(aa);
    if (isNaN(cc)) cc = 0;
    dd=convertToActualSize(bb)
    if (isNaN(dd)) dd = 0;
   
    
    return cc-dd;
    
   
}

function convertToActualSize(val)
{
 var size=0;
 var retval=0;
 var newVal=normalizeString(val);
 

 
 
 if (newVal.match(/KB$/))
 {
   
   var tempstr=newVal.substring(0,newVal.length-2);    
   size = parseFloat(tempstr);
   retval=size*1024;
 }
 if (newVal.match(/MB$/))
 {
         
   var tempstr=newVal.substring(0,newVal.length-2);      
   size = parseFloat(tempstr);
      retval=size*1024*1024;
 }
 if (newVal.match(/GB$/))
 {

 	var tempstr=newVal.substring(0,newVal.length-2);
    size = parseFloat(tempstr);
    retval=size*1024*1024*1024;
 }
 
  return retval;
}



function ts_sort_default(a,b) {
    aa = ts_getInnerText(a.cells[SORT_COLUMN_INDEX]);
    bb = ts_getInnerText(b.cells[SORT_COLUMN_INDEX]);
    if (aa==bb) return 0;
    if (aa<bb) return -1;
    return 1;
}


function addEvent(elm, evType, fn, useCapture)
{
  if (elm.addEventListener){
    elm.addEventListener(evType, fn, useCapture);
    return true;
  } else if (elm.attachEvent){
    var r = elm.attachEvent("on"+evType, fn);
    return r;
  } else {
    alert("Handler could not be removed");
  }
} 



// Regular expressions for normalizing white space.
var whtSpEnds = new RegExp("^\\s*|\\s*$", "g");
var whtSpMult = new RegExp("\\s\\s+", "g");

function normalizeString(s) {

  s = s.replace(whtSpMult, " ");  // Collapse any multiple whites space.
  s = s.replace(whtSpEnds, "");   // Remove leading or trailing white space.

  return s;
}




function getTextValue(el) {

  var i;
  var s;

  // Find and concatenate the values of all text nodes contained within the
  // element.
  s = "";
  for (i = 0; i < el.childNodes.length; i++)
    if (el.childNodes[i].nodeType == document.TEXT_NODE)
      s += el.childNodes[i].nodeValue;
    else if (el.childNodes[i].nodeType == document.ELEMENT_NODE &&
             el.childNodes[i].tagName == "BR")
      s += " ";
    else
      // Use recursion to get text within sub-elements.
      s += getTextValue(el.childNodes[i]);

  return normalizeString(s);
}


function getElement(el) {


    var tagList = new Object
    for (var i = 1; i < arguments.length; i++)
    { 
      tagList[arguments[i]] = true
    }
      
      
     
    while ((el!=null) && (tagList[el.tagName]==null))
     {  
         el = el.parentNode
        
     }
    return el
  }

	function returnUseStyle() {
		return useStyle
	}
	
	
	
function checkAllTrashItems() 
{
  
  var cbxhan = document.getElementsByName('cbxAllTrash');  
  
  
  if ( cbxhan[0].checked == true )
  {
        var TrashCbxHan = document.getElementsByName('cbxTrash');  
          for (i=0;i<TrashCbxHan.length;i++)
		  {
		      TrashCbxHan[i].checked=true;		      
				
		  }
        
  }
  
  if ( cbxhan[0].checked == false )
  {
        var TrashCbxHan = document.getElementsByName('cbxTrash');  
          for (i=0;i<TrashCbxHan.length;i++)
		  {
		      TrashCbxHan[i].checked=false;		      
				
		  }
        
  }
  
  

}

function checkTrashItem(event)
{
       var cbxhan = document.getElementsByName('cbxAllTrash');  
     
       var TrashCbxHan = document.getElementsByName('cbxTrash');  
       cbxhan[0].checked=false;     
    
     
}


	function highlightSentPkg()
	{
	    
	     var str=normalizeString(document.WebDboxSentPkgForm.sentPkgToLit.value);
	     
	     if ( str != 'NoPackageSelected' )
	     { 
	       //tweak ..for handling long package names..getElmntbyid complains..sigh !
	       //also provided a routine on serverside.. dboxpackageinfo.java	
           var newString=str.replace(new RegExp("\\s", "g"), "_"); 
	     
	       
	       var rowhan = document.getElementById(newString);	    
           rowhan.cells[1].className="rover"
			rowhan.cells[2].className="rover"
			rowhan.cells[3].className="rover"
			rowhan.cells[4].className="rover"
			
	       document.WebDboxSentPkgForm.litSentPkgStatus.value=getLitPkgStatus(str); //used for sending a package	       
	     }
	     
	     
     
	}





function getLitPkgStatus(pname)
{
	
	var table = document.getElementById('myTable');
	
	var testRow = new Array();
	
	//skip the headers
    for (i=1;i<table.rows.length;i++) { 
    	var cell = table.rows[i].cells[1];
    	txt = normalizeString(ts_getInnerText(cell));
    	if ( pname == txt )
    	{
        	
        	return normalizeString(ts_getInnerText(table.rows[i].cells[2]));
        	}
    
    
    }
    
    return -1;
	

}
    
  

function delSentPkgs()
{
 
 
 var filesIndices='nopkgstodelete';
 var txt;
 var table = document.getElementById('myTable');
 
 if (table == null )
 {
   return -1;
 }

  var cbxhan = document.getElementsByName('cbxTrash');
  
  
  //pass the string of pkgs to hide in formfield as below
  //"pkgstodelete,packagenamestart#201end*,packagenamestart#203end*"
  
  for (i=0;i<cbxhan.length;i++)
      {
      
      	if (cbxhan[i].checked==true) 
		{
			if (filesIndices == 'nopkgstodelete')
				filesIndices='pkgstodelete'
				
			var cell = table.rows[i+1];    	    
    	    txt = normalizeString(ts_getInnerText(cell.id));
			filesIndices=filesIndices+','+txt;			
			
		}
		
	  }
      
      document.WebDboxSentPkgForm.packToTrash.value=filesIndices;
      document.WebDboxSentPkgForm.packTrashOper.value='true'
      document.WebDboxSentPkgForm.action='WebDboxSentPackagesDel.do';
      document.WebDboxSentPkgForm.submit();     	 
      
} 

