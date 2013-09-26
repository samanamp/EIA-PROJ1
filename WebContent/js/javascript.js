var token = null;
var email = null;
var loggedInContent = null;

$(document).ready(function() {
	init();
});

function init() {
	$.validator.setDefaults({
		submitHandler : function() {
			sendLogin();
			return false;
		},
		wrapper : "li"
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

	/***************************************************************************
	 * Open Dialog for reminder and register
	 **************************************************************************/
	var frmRegisterValidator = $("#frmSendRegister").validate({
		rules : {
			txtSendRegister : {
				required : true,
				email : true
			}
		},
		errorLabelContainer : $("#frmSendRegister ul#error")
	});

	var frmReminderValidator = $("#frmSendReminder").validate({
		rules : {
			txtSendReminder : {
				required : true,
				email : true
			}
		},
		errorLabelContainer : $("#frmSendReminder ul#error")
	});

	$("#sendRegisterContent,#sendReminderContent").dialog({
		autoOpen : false,
		show : {
			effect : "blind",
			duration : 500
		},
		hide : {
			effect : "explode",
			duration : 500
		},
		resizable : false,
		width : 450,
		modal : true
	});

	$("#sendRegisterContent").dialog("option", "title", "Register");
	$("#sendRegisterContent").dialog("option", "buttons", [ {
		id : "btnSendRegister",
		text : "Register",
		click : function() {
			resetForm($(this));
			if ($("#frmSendRegister").valid()) {
				sendRegister();
			}
		}
	} ]);
	$("#sendRegisterContent").dialog("option", "close", function() {
		frmRegisterValidator.resetForm();
		$(this).find("input").removeClass("error").val("");
		resetForm($(this));
	});

	$("#sendReminderContent").dialog("option", "title",
			"Send password reminder");
	$("#sendReminderContent").dialog("option", "buttons", [ {
		id : "btnSendReminder",
		text : "Send reminder email",
		click : function() {
			resetForm($(this));
			if ($("#frmSendReminder").valid()) {
				sendReminder();
			}
		}
	} ]);
	$("#sendReminderContent").dialog("option", "close", function() {
		frmReminderValidator.resetForm();
		$(this).find("input").removeClass("error").val("");
		resetForm($(this));
	});

	$("#lnkRegister a").bind("click", function() {
		$("#sendRegisterContent").dialog("open");
	});

	$("#lnkReminder a").bind("click", function() {
		$("#sendReminderContent").dialog("open");
	});

}

function resetForm(elem) {
	elem.find(".error").html("");
	elem.find("#sending").remove();
	elem.find(".success").remove();
}

function showLoading(text) {
	return "<span id=\"sending\">" + "<img src=\"css/images/loading.gif\" /> "
			+ text + "</span>";
}
/*******************************************************************************
 * Generic function to send an Ajax request to the Servlets. receives a JS
 * object (settings) containing: form_id, url, data, success
 ******************************************************************************/
function sendAjax(settings) {
	if (settings.form_id == undefined)
		settings.form_id = "";

	if (settings.url == undefined)
		settings.url = "";

	if (settings.data == undefined)
		settings.data = {};

	if (settings.success == undefined)
		settings.success = function() {
		};

	if (settings.loadingText == undefined)
		settings.loadingText = "";

	$
			.ajax({
				type : "POST",
				dataType : "json",
				url : settings.url,
				data : settings.data,
				async : false,
				success : settings.success,
				beforeSend : function() {
					$(settings.form_id).parent(".ui-dialog-content").siblings(
							".ui-dialog-buttonpane").find("button").attr(
							"disabled", true);
					$(settings.form_id).append(
							showLoading(settings.loadingText));
				},
				complete : function() {
					$(settings.form_id).parent(".ui-dialog-content").siblings(
							".ui-dialog-buttonpane").find("button").attr(
							"disabled", false).removeAttr("disabled");
					$(settings.form_id).find("#sending").remove();
					$(settings.form_id).find(settings.form_id + " .success")
							.remove();
				},
				error : function(jqXHR, textStatus, errorThrown) {
					// this should never happen
					$(settings.form_id).find("#sending").remove();
					$(settings.form_id)
							.find("#error")
							.html(
									"<li class=\"error\">An error has ocurred: "
											+ textStatus
											+ ((errorThrown != undefined && errorThrown != "") ? ": "
													+ errorThrown
													: "")
											+ ". <br />Please contact the system administrator.</li>")
							.show();
				}
			});
}

/*******************************************************************************
 * Function to send a request to the Login Servlet it uses the sendAjax function
 ******************************************************************************/
function sendLogin() {
	var settings = {
		form_id : "#frmLogin",
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
						"<li class=\"error\">" + data.error + "</li>").show();
			}
		}
	};
	sendAjax(settings);
}

/*******************************************************************************
 * Generates the content that will appear when the user is logged in
 ******************************************************************************/
function generateLoggedInContent() {
	var new_div = $("<div />", {
		id : "loggedIn"
	});
	new_div
			.append(
					"<p>"
							+ "<span id=\"lnkReset\"><a href=\"javascript:void(0)\">Reset my password</a></span>"
							+ "<span id=\"lnkDelete\"><a href=\"javascript:void(0)\">Delete account</a></span>"
							+ "<span id=\"lnkLogout\"><a href=\"javascript:void(0)\">Logout</a></span>"
							+ "</p>"
							+ "<div id=\"contentLoggedIn\" class=\"ui-widget\">"
							+ "<div class=\"ui-state-highlight ui-corner-all\" style=\"margin-top: 20px; padding: 0 .7em;\">"
							+ "<p><span class=\"ui-icon ui-icon-info\" style=\"float: left; margin-right: .3em;\"></span>"
							+ "<strong>Congratulations!!!</strong>"
							+ "</p>"
							+ "<p> You have completed the registration process and have successfully logged in.</p>"
							+ "<p>Now you can enjoy all our exiting functionality.</p>"
							+ "<p>Things that you can do:</p>" + "<ul>"
							+ "<li>Reset your password</li>"
							+ "<li>Delete your account</li>"
							+ "<li>Logout</li>" + "</ul>" + "<p>Enjoy!! :)</p>"
							+ "</div>" + "<ul class=\"error\"></ul>" + "</div>")
			.hide();

	$("#frmLogin").fadeOut("slow", function() {
		$("#frmLogin").remove();

		$("#content").append(new_div);

		$("#lnkLogout").bind("click", function() {
			sendLogout();
		});

		$("#lnkReset").bind("click", function() {
			$(".success").remove();
			sendReset();
		});

		$("#lnkDelete").bind("click", function() {
			$(".success").remove();
			sendDelete();
		});

		new_div.fadeIn(300);

	});
}

/*******************************************************************************
 * Function to send a request to the Register Servlet it uses the sendAjax
 * function
 ******************************************************************************/
function sendRegister() {
	var settings = {
		form_id : "#frmSendRegister",
		url : "Register",
		data : {
			"email" : $("#txtSendRegister").val()
		},
		success : function(data) {
			if (data.success) {
				$("#frmSendRegister")
						.append(
								"<ul class=\"success\"><li>"
										+ "An Email has been sent to your address. Please check your inbox and click "
										+ "in the confirmation link to activate your account."
										+ "</li></ul>");
			} else {
				$("#frmSendRegister #error").html(
						"<li class=\"error\">" + data.error + "</li>").show();
			}
		},
		loadingText : "Registering user..."
	};
	sendAjax(settings);
}

/*******************************************************************************
 * Function to send a request to the Remider Servlet it uses the sendAjax
 * function
 ******************************************************************************/
function sendReminder() {
	var settings = {
		form_id : "#frmSendReminder",
		url : "Reminder",
		data : {
			"email" : $("#txtSendReminder").val()
		},
		success : function(data) {
			if (data.success) {
				$("#frmSendReminder")
						.append(
								"<ul class=\"success\"><li>"
										+ "Your password has been sent to your address please check your inbox and try to login."
										+ "</li></ul>");
			} else {
				$("#frmSendReminder #error").html(
						"<li class=\"error\">" + data.error + "</li>").show();
			}
		},
		loadingText : "Sending reminder..."
	};
	sendAjax(settings);
}

/*******************************************************************************
 * Function to send a request to the Logout Servlet it uses the sendAjax
 * function
 ******************************************************************************/
function sendLogout() {
	if (email != null && token != null) {
		var settings = {
			form_id : "#frmLogin",
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
				generateLoggedOutContent(error, "error");
			}
		};
		sendAjax(settings);
	} else {
		generateLoggedOutContent("Invalid token.", "error");
	}
}

/*******************************************************************************
 * Generates the content that will appear when the user is logged out
 ******************************************************************************/
function generateLoggedOutContent(message, messageClass) {
	$("#loggedIn").fadeOut(
			500,
			function() {
				$("#loggedIn").remove();
				var lic = $(loggedInContent);
				lic.hide();
				$("#content").append(lic);

				if (message != null) {
					if (messageClass == "error") {
						$("#frmLogin #error").html(
								"<li class=\"error\">" + message + "</li>")
								.show();
					} else {
						$("#frmLogin").before(
								"<ul class=\"success\"><li>" + message
										+ "</li></ul>");
					}
				}

				init();
				lic.fadeIn(300);
			});
}

/*******************************************************************************
 * Function to send a request to the Reset Servlet it uses the sendAjax function
 ******************************************************************************/
function sendReset() {
	if (email != null && token != null) {
		var settings = {
			form_id : "#frmLogin",
			url : "Reset",
			data : {
				"email" : email,
				"token" : token
			},
			success : function(data) {
				if (data.success) {
					$("#contentLoggedIn")
							.before(
									"<ul class=\"success\"><li>"
											+ "Your new password has been sent to your address please check your inbox and try to login."
											+ "</li></ul>");
				} else {
					generateLoggedOutContent(data.error, "error");
				}
			}
		};
		sendAjax(settings);
	} else {
		generateLoggedOutContent("Invalid token.", "error");
	}
}

/*******************************************************************************
 * Function to send a request to the Delete Servlet it uses the sendAjax
 * function
 ******************************************************************************/
function sendDelete() {
	if (email != null && token != null) {
		var settings = {
			form_id : "#frmLogin",
			url : "Delete",
			data : {
				"email" : email,
				"token" : token
			},
			success : function(data) {
				if (data.success) {
					generateLoggedOutContent(
							"We are sorry that you have decided to leave us. "
									+ "We hope you come back soon :)",
							"success");
				} else {
					generateLoggedOutContent(data.error, "error");
				}
			}
		};
		sendAjax(settings);
	} else {
		generateLoggedOutContent("Invalid token.", "error");
	}
}