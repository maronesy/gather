var signedIn = false;

function resizeMap() {
	var cw = $('#map-canvas').width()*.75;
	$('#map-canvas').css({'height':cw+'px'});
}

function resizeLayout() {
	$('#resizeLayout').on('click', function() {
		if ($('#rightPane').hasClass("col-lg-7")) {
			$('#resizeLayout').removeClass('glyphicon-resize-small').addClass('glyphicon-resize-full');
			$('#rightPane').switchClass("col-lg-7", "col-lg-5");
			setTimeout(
					function(){
						$('#leftPane').switchClass("col-lg-5", "col-lg-7");
					}, 200);
			setInterval(
					function(){
						resizeMap();
					}, 10);
		} else if ($('#rightPane').hasClass("col-lg-5")) {
			$('#resizeLayout').removeClass('glyphicon-resize-full').addClass('glyphicon-resize-small');
			$('#leftPane').switchClass("col-lg-7", "col-lg-5");
			setTimeout(
					function(){
						$('#rightPane').switchClass("col-lg-5", "col-lg-7");
					}, 200);
			setInterval(
					function(){
						resizeMap();
					}, 10);
		}
		clearInterval(1000);
	});
}

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
//	$('#enterZip').mousedown(function() {
//		$('#zipSearching').show();
//	});
//	
//	$('#enterZip').keydown(function() {
//		$('#zipSearching').show();
//	});
	
	$('#enterZip').on('click', function() {
		
		var zipcode = $('#zipCode').val();
		var zipCodeErrorBox = $(this).attr('href');
		$('#zipSearching').show();
		
		setTimeout(
			function(){
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
				$('#zipSearching').hide();
			}, 100)

		
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
		resetRegisterFields()
		return false;
	});

}

function signIn() {
	$('#loginFormSubmit').on(
			'click',
			function() {
				var email = $("#signInEmail").val();
				var password = $("#signInPassword").val();
				$.ajax({
				 	accepts: "application/json",
					type : "POST",
					url : "rest/registrants/signin",
					contentType: "application/json; charset=UTF-8",
					dataType: "json",
					data : '{ \
						"email" : "' + email + '", \
						"password" : "' + password + '" \
					}',
					success : function(returnvalue) {
						if (returnvalue.status == 0) {
//							alert("Sign In Successful");
							resetSignInFields()
							signedIn = true
							gather.global.currentDisplayName = returnvalue.displayName;
							updateGreeting();
							headerSelect()
						} else {
//							alert(returnvalue.status)
//							alert(returnvalue.message)
//							alert("Sign In Unsuccessful")
							resetSignInFields()
						}
					}
				});
			});

}

function signOut() {
	$('#signOutButton').on(
			'click',
			function() {
				 $.ajax({
					 	accepts: "application/json",
						type : "POST",
						url : "rest/registrants/signout",
						contentType: "application/json; charset=UTF-8",
						success : function(returnvalue) {
							if (returnvalue.status == 0) {
//								alert(returnvalue.status)
//								alert(returnvalue.message)
								signedIn = false;
								headerSelect()
							} else {
								if (returnvalue.status != 0) {
//									alert(returnvalue.status)
//									alert(returnvalue.message)
								}
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
				var registerBox = $('#registerFormSubmit').attr('href');
				if (displayName == "" || password == "" || confirmPassword == "" || email == "") {
					$('#formFeedback').html('All the fields are required');
				} else if (validate_email(email) == false) {
					$('#formFeedback').html(
							'Please enter a valid email address');
				} else if (validatePass(password) == false) {
					$('#formFeedback').html(
							'Password must be between 6 and 21 characters');
				} else if (password != confirmPassword) {
					$('#formFeedback').html('Passwords do not match');
				} else if (validateDisplayName(displayName) == false) {
					$('#formFeedback').html('Display name must be between than 5 and 15 characters');
				} else {
				 $.ajax({
					 	accepts: "application/json",
						type : "POST",
						url : "/rest/registrants",
						contentType: "application/json; charset=UTF-8",
						dataType: "json",
						beforeSend: function() {
							$('#loading').show();
						},
						data : '{ \
							"email" : ' + email + ', \
							"password" : ' + password + ', \
							"displayName" : ' + displayName + ' \
						}',
						complete: function() {
							$('#loading').hide();
						},
						success : function(returnvalue) {
							if (returnvalue.status == 0) {
								$(registerBox).fadeOut(100);
								$('#mask , .register-popup').fadeOut(300, function() {
									$('#mask').remove();
								});
								resetRegisterFields();
								signedIn = true
								gather.global.currentDisplayName = displayName;
								updateGreeting();
								headerSelect();
//								alert('Registration success.');
							} else {
								if (returnvalue.status != 0) {
//									alert(returnvalue.status)
//									alert(returnvalue.message)
									$('#form_feedback').html('This email is in use.');
									
								}
							}
						}
					});
					
				}
			});
}

function sessionCheck() {
	$.ajax({
	 	accepts: "application/json",
		type : "GET",
		url : "rest/session",
		contentType: "application/json; charset=UTF-8",
		success : function(returnvalue) {
			if (returnvalue.status == 5) {
				signedIn = true;
				headerSelect();
//				alert(returnvalue.status);
//				alert(returnvalue.message)
			} 
		},
		error: function(jqXHR, textStatus, errorThrown) {
//		    alert(jqXHR.status);
//		    alert(textStatus);
//		    alert(errorThrown);
			if (errorThrown == "Found") {
				signedIn = true;
				gather.global.currentDisplayName = jqXHR.responseJSON.displayName;
				updateGreeting();
				headerSelect();
			} else {
				signedIn = false;
				headerSelect();
			}

		}
	});
}




function updateGreeting(){
	document.getElementById("greetings").innerHTML = "Welcome "+gather.global.currentDisplayName;
}

function onLoadSessionCheck() {
	//alert(signedIn);
	$(window).unload(function() {
		headerSelect();
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

function validatePass(password){
	if (password.length < 7 || password.length > 21) {
		return false;
	} else {
		return true;
	}
}

function validateDisplayName(displayName){
	if (displayName.length < 5 || displayName.length > 15) {
		return false;
	} else {
		return true;
	}
}


function headerSelect() {
	if (signedIn == true) {
		$("#headerOut").hide();
		$("#headerIn").show();
	} else if (signedIn == false){
		$("#headerIn").hide();
		$("#headerOut").show();
	}
}

function resetSignInFields() {
	document.getElementById("signingin").reset();
	//$("#signingin").reset();
	return;
}

function resetRegisterFields() {
	document.getElementById("registration").reset();
	//$("#registration").reset();
	return;
}

