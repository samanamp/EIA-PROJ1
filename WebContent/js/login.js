$(document).ready(function() {

	$("#txtEmail,#txtPassword").bind("keypress", function(e) { 
		if (e.which == 13) 
			sendLogin(); 
	});
	
	$("#btnLogin").bind("click", function() {
		
		//TODO validate email and password
		sendLogin();
		
	});
});

function sendLogin() {
	$.getJSON("Login", {
		"email" : $( "#txtEmail" ).val(),
		"password" : $( "#txtPassword" ).val()
	}, callbackLogin);
}
function callbackLogin( data ) {
	if( data.success ) {
		location.href = "home.html";
	} else {
		alert(data.error);
	}
}