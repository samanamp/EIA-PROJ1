function verifySession(current) {
	$.ajax({
	  dataType: "json",
	  url: "verifySession",
	  data: {
		  current: current
		  },
	  async: false,
	  success: function ( data ) {
		if( data.redirect != current ) {
			location.href = data.redirect;
		}
	  }
	});
}