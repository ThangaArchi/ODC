//addEvent(window, "load", clearthis);
//addEvent(window, "load", createListObjects);


var selectedList;
var availableList;

// Regular expressions for normalizing white space.
var whtSpEnds = new RegExp("^\\s*|\\s*$", "g");
var whtSpMult = new RegExp("\\s\\s+", "g");

function normalizeString(s) {

  s = s.replace(whtSpMult, " ");  // Collapse any multiple whites space.
  s = s.replace(whtSpEnds, "");   // Remove leading or trailing white space.

  return s;
}


function getElementByName(name) {
   // return document.getElementById(name);
   elms = document.getElementsByName(name);
   if (elms && elms.length > 0) {
      return elms[0];
   }
   return null;
}


function createListObjects(){
    clearthis();
    availableList = getElementByName("savedList");
    selectedList = getElementByName("sendtoList"); 
}


//function createListObjects(){    
//   alert(".........createListObjects called ...");
//   // clearthis();
//    availableList = document.getElementById('savedList');
//    selectedList = document.getElementById('sendtoList'); 
//    //availableList = document.getElementsByName("savedList");
//    //selectedList = document.getElementsByName("sendtoList"); 
//}

function addEvent(elm, evType, fn, useCapture)
// addEvent and removeEvent
// cross-browser event handling for IE5+,  NS6 and Mozilla

{
  if (elm.addEventListener){
    //alert("NS6 and Mozilla event"); 
    elm.addEventListener(evType, fn, useCapture);
    return true;
  } else if (elm.attachEvent){
   // alert("IE5+ event"); 
    var r = elm.attachEvent("on"+evType, fn);
    return r;
  } else {
    alert("Handler could not be removed");
  }
} 









function delRecipient()
{
  
	var selIndex = selectedList.selectedIndex;
	if(selIndex < 0)
      return;
     selectedList.remove(selIndex);
     selectNone(selectedList,availableList);
	 setSize(availableList,selectedList);
   	 updateAclField();
}


function checkIfAttributeUsrOrGrpExistsInSelectedList(userOrgroup)
 {
  var returnValue = false;
   
   var len = selectedList.length;
 	 for(i=0; i<len; i++){
       
        var listitem=normalizeString(selectedList.item(i).value);
        userOrgroup=normalizeString(userOrgroup);
        
        if ( userOrgroup == listitem )
          returnValue = true;
    }
   
  return returnValue;
 }
 
 function checkIfAttributeUsrOrGrpExistsInAvailableList(userOrgroup)
 {
  var returnValue = false;
   
   var len = availableList.length;
 	 for(i=0; i<len; i++){
       
        var listitem=normalizeString(availableList.item(i).value);
        userOrgroup=normalizeString(userOrgroup);
        
        if ( userOrgroup == listitem )
          returnValue = true;
    }
   
  return returnValue;
 }



function delAttribute(){
   var selIndex = selectedList.selectedIndex;
   if(selIndex < 0)
      return;

    //  check if an items already appended in a list not to be added....
  	 var dupExists=checkIfAttributeUsrOrGrpExistsInAvailableList(selectedList.options.item(selIndex).value);
 	 if ( dupExists == false )
  	 { 
	   availableList.appendChild(selectedList.options.item(selIndex))
	   selectNone(selectedList,availableList);
	   setSize(availableList,selectedList);
	   updateAclField();
	 }else
	 {
	   selectedList.remove(selIndex);
	   selectNone(selectedList,availableList);
	   setSize(availableList,selectedList);
	 } 
	 
}

function addAttribute(){
   
 // availableList = document.getElementById('savedList');
//  selectedList = document.getElementById('sendtoList');  

   var addIndex = availableList.selectedIndex;
   if(addIndex < 0)
      return;
    
      // check if an items already appended in a list not to be added....
  	 var dupExists=checkIfAttributeUsrOrGrpExistsInSelectedList(availableList.options.item(addIndex).value);
 	 if ( dupExists == false )
  	 {        
	   selectedList.appendChild(availableList.options.item(addIndex));
	   selectNone(selectedList,availableList);
	   setSize(selectedList,availableList);
	   updateAclField();
	 }else
	 {
	   availableList.remove(addIndex);
	   selectNone(selectedList,availableList);
	   setSize(selectedList,availableList);
	 } 
}

function setTop(top){
	document.getElementById
      ('someLayer').style.top = top;
}
function setLayerTop(lyr,top){
	lyr.style.top = top;
}

function setSize(list1,list2){
    list1.size = getSize(list1);
    list2.size = getSize(list2);
}

function selectNone(list1,list2){
    list1.selectedIndex = -1;
    list2.selectedIndex = -1;
    addIndex = -1;
    selIndex = -1;
}


function getSize(list){
    /* Mozilla ignores whitespace, 
       IE doesn't - count the elements 
       in the list */
    var len = list.childNodes.length;
    var nsLen = 0;
    //nodeType returns 1 for elements
    for(i=0; i<len; i++){
        if(list.childNodes.item(i).nodeType==1)
            nsLen++;
    }
    if(nsLen<2)
        return 2;
    else
        return nsLen;
}

function delAll(){
    var len = selectedList.length -1;
    for(i=len; i>=0; i--){
        availableList.appendChild(selectedList.item(i));
    }
    selectNone(selectedList,availableList);
    setSize(selectedList,availableList);
    
}

function addAll(){
    var len = availableList.length -1;
    for(i=len; i>=0; i--){
        selectedList.appendChild(availableList.item(i));
    }
    selectNone(selectedList,availableList);
    setSize(selectedList,availableList);
    
}




function showSelected(){
   // var optionList = document.getElementById("savedList").options;
var optionList = getElementByName("savedList").options;
    var data = '';
    var len = optionList.length;
    for(i=0; i<len; i++){
        data += ',';
        data += optionList.item(i).value;
    }
    alert(data);
}


 function callutiljs(){

 var sel = document.getElementById("testexpire");
 //var sel = document.getElementByName("expirationsOptions");
 var user_input=sel.options[sel.selectedIndex].value;
 document.WebDboxCreateDraftPkgForm.expire.value=user_input; 
 
 }
 
 
 function checkIfAttributeUsrOrGrpExists(userOrgroup)
 {
  var returnValue = false;
   
   var len = selectedList.length;
 	 for(i=0; i<len; i++){
       
        var listitem=normalizeString(selectedList.item(i).value);
        userOrgroup=normalizeString(userOrgroup);       
        
        if ( userOrgroup == listitem )
          returnValue = true;
    }
   
  return returnValue;
 }
 
 
 
 
 function appendAttribute()
 {
 	
	if ( document.WebDboxCreateDraftPkgForm.typeOfId.value == 'none' ) 
	alert("Please select a recipient type !")
	else {
		 var itemval=document.WebDboxCreateDraftPkgForm.iccid.value;
		 
		 var item=itemval.toLowerCase();
		 if ( item != "" ){
		 var val=document.WebDboxCreateDraftPkgForm.typeOfId.value+' '+item; //default val of typeOfId is none
	   
	   
	  	 //check if an items already appended in a list not to be added....
	  	 var dupExists=checkIfAttributeUsrOrGrpExists(val);
	 	 if ( dupExists == false )
	  	 {	selectedList.options[selectedList.options.length] = new Option(val,val); 
	 	    updateAclField();
	 	 }
	 	 WebDboxCreateDraftPkgForm.iccid.value=""

	 	 
	 	}
	 	
	  	 	
 	}
   
 }
 
 
 
 function updateAclField()
 {
 			
 	   
 		//var optionList = document.getElementById("sendtoList").options;
                var optionList = getElementByName("sendtoList").options;
 	 	var data = ''; // need to clear duplicates...
		var datum = ''; // need to clear duplicates...    
	    var len = optionList.length;
	    
	    if ( len == 0 )
	    {
	       document.WebDboxCreateDraftPkgForm.aclList.value='empty';
	       document.WebDboxCreateDraftPkgForm.aclListRcpt.value='empty';
	       return;
	    
	    }
	    
	    for(i=0; i<len; i++){
	        data += ',';
	        data += optionList.item(i).text;
	        var strObj = new String(optionList.item(i).text);
	       
	        if ( strObj.indexOf("[USER]") == 0 )
	        {
	           var newString=strObj.replace("[USER] ","_U_")
	           
	            datum += ',';
	            datum += newString;
	        }
	        if ( strObj.indexOf("[GROUP]") == 0 )
	        {
	           var newString=strObj.replace("[GROUP] ","_G_")
	          
	            datum += ',';
	            datum += newString;
	        }
	      
	    }
	    
	   // alert(data);
	   // alert(datum);
	    
	    document.WebDboxCreateDraftPkgForm.aclList.value=data;
	    document.WebDboxCreateDraftPkgForm.aclListRcpt.value=datum;
 
 
 }
 
 
 
 function addFilesToPackage()
 {
 	
 	document.WebDboxCreateDraftPkgForm.addFiles.value='true';
 	document.WebDboxCreateDraftPkgForm.submit();
 
 
 }

function submitCreatPkgOper()
{

updateAclField();
document.WebDboxCreateDraftPkgForm.submit();
}


function radIccId_onclick(radIndex)
{
   var returnValue = true;
  
   if (radIndex == 0 )
   {
  	 
     document.WebDboxCreateDraftPkgForm.radIccId[radIccIdIndex].value='user';	
     var val=document.WebDboxCreateDraftPkgForm.radIccId[radIccIdIndex].value;
     alert(val);
   }
   if (radIndex == 1 )
   {
  	 
     document.WebDboxCreateDraftPkgForm.radIccId[radIccIdIndex].value='group';
     var val=document.WebDboxCreateDraftPkgForm.radIccId[radIccIdIndex].value;
     alert(val);
   }
   
  
      radIccIdIndex = radIndex;
  
   return returnValue;
}



 var radIccIdIndex = 0;
function checkRadUserIdChoice(radIndex)
{

  
  var returnValue = true;
	
  if (radIndex ==0)
   {
		if ( document.WebDboxCreateDraftPkgForm.addIccIdUser.checked == true ){
			document.WebDboxCreateDraftPkgForm.addIccIdGroup.checked = false;			
			document.WebDboxCreateDraftPkgForm.typeOfId.value=document.WebDboxCreateDraftPkgForm.addIccIdUser.value
		}
  }
   if (radIndex == 1)
   {
		if ( document.WebDboxCreateDraftPkgForm.addIccIdGroup.checked == true ){
   		    document.WebDboxCreateDraftPkgForm.addIccIdUser.checked = false;			
			document.WebDboxCreateDraftPkgForm.typeOfId.value=document.WebDboxCreateDraftPkgForm.addIccIdGroup.value
		}
   }
   
  
      radIccIdIndex = radIndex;
  
   return returnValue;
	
}

function clearthis()
{	
    	document.WebDboxCreateDraftPkgForm.addIccIdUser.checked = true;
	document.WebDboxCreateDraftPkgForm.typeOfId.value=document.WebDboxCreateDraftPkgForm.addIccIdUser.value
	document.WebDboxCreateDraftPkgForm.addIccIdGroup.checked = false;
}