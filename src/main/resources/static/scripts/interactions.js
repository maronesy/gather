function tableInteractions() {
	$('.star').on('click', function() {
		$(this).toggleClass('star-checked');
	});

	$('.ckbox label').on('click', function() {
		$(this).parents('tr').toggleClass('selected');
	});

	$('.btn-filter').on('click', function() {
		var $target = $(this).data('target');
		if ($target != 'all') {
			$('.table tr').css('display', 'none');
			$('.table tr[data-status="' + $target + '"]').fadeIn('slow');
		} else {
			$('.table tr').css('display', 'none').fadeIn('slow');
		}
	});
}

function locateMe() {
	$('#locateMe').on('click', function() {
		mapManager.performAction();
	});
}

function enterZip() {
	$('#enterZip').on('click', function() {
		var zipcode = $('#zipCode').val();
		var zipCodeErrorBox = $(this).attr('href');
		
		if (zipcode == "") {
			$(zipCodeErrorBox).fadeIn(100);
			$('#zipCodeErrorBox').html('Zip code field is empty');
		} else if (zipcode.length != 5) {
			$(zipCodeErrorBox).fadeIn(100);
			$('#zipCodeErrorBox').html('Zip code must be five digits');
		} else if (!($.isNumeric(zipcode))) {
			$(zipCodeErrorBox).fadeIn(100);
			$('#zipCodeErrorBox').html('Zip code must be five digits');
		} else {
			var returnValue = mapManager.determineCoordByZipCode(zipcode);
			if (returnValue == -1) {
				$(zipCodeErrorBox).fadeIn(100);
				$('#zipCodeErrorBox').html('Zip code does not exist');
			}
		}
	});
}

function removeZipCodeError() {
	$('#zipCode').on('focus', function() {
		$(zipCodeErrorBox).fadeOut(100);
	});
}

function registerBox() {
	$('#registerButton').on('click', function() {

		// Getting the variable's value from a link
		var registerBox = $(this).attr('href');

		// Fade in the Popup
		$(registerBox).fadeIn(300);

		// Set the center alignment padding + border see css style
		var popMargTop = ($(registerBox).height() + 24) / 2;
		var popMargLeft = ($(registerBox).width() + 24) / 2;

		$(registerBox).css({
			'margin-top' : -popMargTop,
			'margin-left' : -popMargLeft
		});

		// Add the mask to body
		$('body').append('<div id="mask"></div>');
		$('#mask').fadeIn(300);

		return false;
	});

	// When clicking on the button close or the mask layer the popup closed
	$('a.close-button, #mask').on('click', function() {
		// $('#nickname').val('');
		// $('#password').val('');
		// $('#password_again').val('');
		// $('#code').val('');
		// $('#form_feedback').html('');
		$('#mask , .register-popup').fadeOut(300, function() {
			$('#mask').remove();
		});
		return false;
	});

}

//function signIn() {
//	$('#loginFormSubmit').on(
//			'click',
//			function() {
//				var email = $("#signInEmail").val();
//				var password = $("#signInPassword").val();
//
//				$.ajax({
//						type : "POST",
//						url : "api/registereds",
//						contentType: "application/json",
//						data : '{ \
//							"firstName": "Tom", \
//							"lastName": "jenkins", \
//							"age": 30, \
//							"location": "San Diego" \
//						}',
//						success : function(returnvalue) {
//							if (returnvalue == "success") {
//								alert("Sign In Successful")
//							}
//						}
//					});
//				});
//}

function signIn() {
	$('#loginFormSubmit').on(
			'click',
			function() {
				var email = $("#signInEmail").val();
				var password = $("#signInPassword").val();

				$.ajax({
						accepts: "application/json",
						type : "POST",
						url : "sign-in",
						contentType: "application/json; charset=UTF-8",
						dataType: "json",
						data : '{ \
							"username" : "email", \
							"password" : "password", \
						}',
						success : function(returnvalue) {
							if (returnvalue == "success") {
								alert("Sign In Successful")
							}
						}
					});
				});
}

function signUp() {
	$('#registerFormSubmit').on(
			'click',
			function() {
				var email = $("#inputEmail").val();
				var password = $("#inputPassword1").val();
				var confirmPassword = $("#inputPassword2").val();
				var displayName = $("#inputDisplayName").val();
				if (displayName == "" || password == "" || confirmPassword == "" || email == "") {
					$('#formFeedback').html('All the fields are required');
				} else if (validate_email(email) == false) {
					$('#formFeedback').html(
							'Please enter a valid email address');
				} else if (password.length < 7) {
					$('#formFeedback').html(
							'Password must be more than 6 characters');
				} else if (password.length > 21) {
					$('#formFeedback').html(
							'Password must be less than 20 characters');
				} else if (password != confirmPassword) {
					$('#formFeedback').html('Passwords do not match');
				} else if (displayName.length < 5) {
					$('#formFeedback').html('Display name must be more than 5 characters');
				} else if (displayName.length > 15) {
					$('#formFeedback').html('Display name must be less than 5 characters');
				} else {
				 $('#loading').show();
				 $.ajax({
					 	accepts: "application/json",
						type : "POST",
						url : "register",
						contentType: "application/json; charset=UTF-8",
						dataType: "json",
						data : '{ \
							"email" : ' + email + ', \
							"password" : ' + password + ', \
							"displayName" : ' + displayName + ' \
						}',
						success : function(returnvalue) {
							if (returnvalue == 0) {
								alert('Registration success.')
							} else {
								if (returnvalue == -1) {
									$('#form_feedback').html('This email is in use.');
								}
							}
						}
					});
					$('#loading').hide();
				}
			});
}

function validate_email(email) {
	var x = email;
	var atpos = x.indexOf("@");
	var dotpos = x.lastIndexOf(".");
	if (atpos < 1 || dotpos < atpos + 2 || dotpos + 2 >= x.length) {
		return false;
	} else {
		return true;
	}
}