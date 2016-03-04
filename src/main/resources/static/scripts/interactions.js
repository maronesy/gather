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
		var zipcode = $('#zipcode').val();
		mapManager.determineCoordByZipCode(zipcode);
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

function signUp() {
	$('#registerFormSubmit').on(
			'click',
			function() {
				var fn = $("#InputFirstName").val();
				var ln = $("#InputLastName").val();
				var e = $("#InputEmail").val();
				var p = $("#InputPassword1").val();
				var cp = $("#InputPassword2").val();
				if (ln == "" || fn == "" || p == "" || cp == "" || e == "") {
					$('#formFeedback').html('All the fields are required');
				} else if (validate_email(e) == false) {
					$('#formFeedback').html(
							'Please enter a valid email address');
				} else if (p.length < 7) {
					$('#formFeedback').html(
							'Password must be more than 6 characters');
				} else if (p.length > 21) {
					$('#formFeedback').html(
							'Password must be less than 20 characters');
				} else if (p != cp) {
					$('#formFeedback').html('Your passwords do not match');
				}
				// else {
				// $('#loading').show();
				// $.ajax({
				// type: "POST",
				// url: "includes/non_user/popup_registrationform",
				// data: {nickname:n, password:p, password_again:cp, code:cd},
				// success: function(returnvalue) {
				// if(returnvalue == "success") {
				// setTimeout(function(){
				// $('#signin-nickname').val(n);
				// $('#signin-password').val(p);
				// $('#signin').click();
				// },2000);
				// } else {
				// $('#form_feedback').html(returnvalue);
				// }
				// }
				// });
				// $('#loading').hide();
				// }
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