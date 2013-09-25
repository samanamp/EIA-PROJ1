var token = null;
var email = null;
var loggedInContent = null;
var sendingEmail = "<span id=\"sending\">"
	+ "<img src=\"css/images/loading.gif\" /> Sending email..."
	+ "</span>";
$(document).ready(function() {
	init();
});

function init() {
	$.validator.setDefaults({
		submitHandler : function() {
			sendLogin();
			return false;
		},
		wrapper: "li"
	});

	$("#frmLogin").validate({
		rules : {
			txtEmail : {
				required : true,
				email : true
			},
			txtPassword : {
				required : true
			}
		},
		errorLabelContainer : $("#frmLogin ul#error")
	});

	$("#txtEmail,#txtPassword").bind("keypress", function(e) {
		$("#frmLogin #error").html("").hide();
		if (e.which == 13)
			sendLogin();
	});

	$("#btnLogin").button({
		text : "Login"
	});

	loggedInContent = $("#content").html();
	
	/*****************************************
	 * Open Dialog for reminder and register
	 *****************************************/
	// TODO validate fields
	var frmEmailValidator = $("#frmSendConfirmation").validate({
		rules: {
			txtSendConfirmation: {
				required: true,
				email: true
			}
		},
		errorLabelContainer : $("#frmSendConfirmation ul#error")
	});
	
	var frmEmailReminderValidator = $("#frmSendReminder").validate({
		rules: {
			txtSendReminder: {
				required: true,
				email: true
			}
		},
		errorLabelContainer : $("#frmSendReminder ul#error")
	});
	
	$("#sendConfirmationContent,#sendReminderContent").dialog({
		autoOpen : false,
        show: {effect: "blind",duration: 500},
        hide: {effect: "explode",duration: 500},
        resizable: false,
		width : 450,
		modal : true,
		close : function() {
			frmEmailValidator.resetForm();
			frmEmailReminderValidator.resetForm();
			$(this).find("#error").html("");
			$(this).find("input").removeClass("error").val("");
			$(this).find("#sending").remove();
			$(this).find("#success").remove();
		}
	});
	
	$( "#sendConfirmationContent" ).dialog( "option", "title", "Register" );	
	$( "#sendConfirmationContent" ).dialog( "option", "buttons", [ {
		id: "btnSendConfirmation",
    	text: "Register", 
		click: function(){
			if($("#frmSendConfirmation").valid()){
				sendConfirmation();
			}
		} 
    }] );
	
	
	$( "#sendReminderContent" ).dialog( "option", "title", "Send password reminder" );
	$( "#sendReminderContent" ).dialog( "option", "buttons", [ { 
		id: "btnSendReminder",
    	text: "Send reminder email", 
		click: function(){
			if($("#frmSendReminder").valid()){
				sendReminder();
			}
		} 
    }] );

	$("#lnkRegister a").bind("click", function() {
		$("#sendConfirmationContent").dialog("open");
	});
	
	$("#lnkReminder a").bind("click", function() {
		$("#sendReminderContent").dialog("open");
	});
	
}

/***********************************************************
 * Generic function to send an Ajax request to the Servlets
 * receives a JS object (settings) containing:
 * url, data, success, beforeSend, complete 
 ***********************************************************/
function sendAjax(settings){
	if(settings.form_id == undefined)
		settings.form_id = "";
	
	if(settings.url == undefined)
		settings.url = "";
	
	if(settings.data == undefined)
		settings.data = {};
	
	if(settings.success == undefined)
		settings.success = function(){};
	
	if(settings.beforeSend == undefined)
		settings.beforeSend = function(){};
	
	if(settings.complete == undefined)
		settings.complete = function(){};
	
	if(settings.error == undefined)
		settings.error = function(){};
		
	$.ajax({
		type : "POST",
		dataType : "json",
		url : settings.url,
		data : settings.data,
		async : false,
		success : settings.success,
		beforeSend : settings.beforeSend,
		complete : settings.complete,
		error : function( jqXHR, textStatus, errorThrown){
			//this should never happen
			$("#sending").remove();
			$("#" + settings.form_id + " #error").html(
					"<label class=\"error\">An error has ocurred: " + textStatus + 
					((errorThrown != undefined && errorThrown != "") ? ": " + errorThrown : "") + 
					". <br />Please contact the system administrator.</label>")
					.show();
		}
	});
}

/***********************************************************
 * Function to send a request to the Login Servlet
 * it uses the sendAjax function
 ***********************************************************/
function sendLogin() {
	var settings = {
		form_id : "frmLogin",
		url : "Login",
		data : {
			"email" : $("#txtEmail").val(),
			"password" : $("#txtPassword").val()
		},
		success : function(data) {
			if (data.success) {
				token = data.token;
				email = $("#txtEmail").val();
				generateLoggedInContent();
			} else {
				$("#frmLogin #error").html(
						"<label class=\"error\">" + data.error + "</label>")
						.show();
				if($("lnkRegisterHere").length > 0){
					$("lnkRegisterHere").bind("click", function(){});
				}
			}
		},
		beforeSend : function() {
			$("#btnLogin").attr("disabled", true);
			$("#frmLogin #error").html(sendingEmail);
		},
		complete : function() {
			$("#btnLogin").attr("disabled", false);
		}
	};
	sendAjax(settings);
}

/***********************************************************
 * Generates the content that will appear when the user
 * is logged in
 ***********************************************************/
function generateLoggedInContent() {
	var new_div = $("<div />", {
		id : "loggedIn"
	});
	new_div
			.append("<p>"
					+ "<span id=\"lnkLogout\"><a href=\"javascript:void(0)\">Logout</a></span>"
					+ "<span id=\"lnkReset\"><a href=\"javascript:void(0)\">Reset my password</a></span>"
					+ "<span id=\"lnkDelete\"><a href=\"javascript:void(0)\">Delete account</a></span>"
					+ "</p>");
	new_div.hide();

	$("#frmLogin").fadeOut("slow", function() {
		$("#frmLogin").remove();

		$("#content").append(new_div);
		new_div.fadeIn(300);

		$("#lnkLogout").bind("click", function() {
			sendLogout();
		});
		//TODO bind click event to lnkReset and lnkDelete
		
	});
}

/***********************************************************
 * Function to send a request to the Logout Servlet
 * it uses the sendAjax function
 ***********************************************************/
function sendLogout() {
	if (email != null && token != null) {
		var settings = {
			form_id : "frmLogin",
			url : "Logout",
			data : {
				"email" : email,
				"token" : token
			},
			success : function(data) {
				token = null;
				email = null;
				var error = null;
				if (!data.success) {
					error = data.error;
				}
				generateLoggedOutContent(error);
			}
		};
		sendAjax(settings);
	} else {
		generateLoggedOutContent();
	}
}

/***********************************************************
 * Generates the content that will appear when the user
 * is logged out
 ***********************************************************/
function generateLoggedOutContent(errorMessage) {
	$("#loggedIn").fadeOut(
			500,
			function() {
				$("#loggedIn").remove();
				var lic = $(loggedInContent);
				lic.hide();
				$("#content").append(lic);

				if (errorMessage != null) {
					$("#frmLogin #error").html(
							"<label class=\"error\">" + errorMessage
									+ "</label>").show();
				}

				init();
				lic.fadeIn(300);
			});
}

/***********************************************************
 * Function to send a request to the Confirmation Servlet
 * it uses the sendAjax function
 ***********************************************************/
function sendConfirmation() {
	var settings = {
		form_id : "frmSendConfirmation",	
		url : "Confirmation",
		data : {
			"email" : $("#txtSendConfirmation").val()
		},
		success : function(data) {
			if (data.success) {
				//TODO
				$("#frmSendConfirmation").append(
					"<div id=\"success\"><span>" +
					"An Email has been sent to your address. Please check your inbox and click " +
					"in the confirmation link to activate your account." +
					"</span></div>"
				);
			} else {
				$("#frmSendConfimation #error").html(
						"<label class=\"error\">" + data.error + "</label>")
						.show();
			}
		},
		beforeSend : function() {
			$("#btnSendConfirmation").attr("disabled", true);
			$("#frmSendConfirmation").append(sendingEmail);
		},
		complete : function() {
			$("#btnSendConfirmation").attr("disabled", false);
		}
	};
	sendAjax(settings);
}

/***********************************************************
 * Function to send a request to the Remider Servlet
 * it uses the sendAjax function
 ***********************************************************/
function sendReminder() {
	var settings = {
		form_id : "frmSendReminder",
		url : "Reminder",
		data : {
			"email" : $("#txtEmail").val()
		},
		success : function(data) {
			if (data.success) {
				$("#frmSendReminder").append(
						"<div id=\"success\"><span>" +
						"Your password has been sent to your address please check your inbox and try to login." +
						"</span></div>"
					);
			} else {
				$("#frmSendReminder #error").html(
						"<label class=\"error\">" + data.error + "</label>")
						.show();
			}
		},
		beforeSend : function() {
			$("#btnSendRemider").attr("disabled", true);
			$("#frmSendReminder").append(sendingEmail);
		},
		complete : function() {
			$("#btnSendReminder").attr("disabled", false);
		}
	};
	sendAjax(settings);
}