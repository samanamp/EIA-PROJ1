$(document).ready(function() {

	$("#lnkLogout").bind("click", function() {

		// TODO validate email and password

		$.getJSON("Logout", {}, callbackLogout);
	});
});

function callbackLogout(data) {
//	if (data.success) {
		location.href = "/proj1";
//	} else {
//		alert(data.error);
//	}
}