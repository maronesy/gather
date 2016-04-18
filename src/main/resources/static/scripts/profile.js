$(document).ready(function() {
	loadProfilePage();
	rightPaneSelect();
	loadProfilePasswordForm();
	submitProfile();
});

function rightPaneSelect() {
	$("#profile").hide();
	$("#map").show();
}

function loadProfilePage() {
	$('#myProfile').on('click', function() {
		$("#profile").show();
		$("#map").hide();
		$.ajax({
		 	accepts: "application/json",
			type : "PUT",
			url : "rest/registrants/info",
			contentType: "application/json; charset=UTF-8",
			dataType: "json",
			data : '{}',
			success : function(returnvalue) {
				if (returnvalue.status == 0) {
					var displayName = returnvalue.result.displayName
					var defaultZip = returnvalue.result.defaultZip
					var defaultTimeWindow = returnvalue.result.defaultTimeWindow
					$("#profileDisplayName").val(displayName)
					$("#profileZipCode").val(defaultZip)
					$("#profileTimeWindow").val(defaultTimeWindow)
					$("#profile").show();
					$("#map").hide();
					updateProfileHeader();
				} else {

				}
			}
		});
	});

	$('#profileBack').on('click', function() {
		$("#profile").hide();
		$("#map").show();
	});
}

function submitProfile() {
	$('#profileSubmit').on('click', function() {
		var displayName = $("#profileDisplayName").val()
		var oldPassword = $('#profileCurrentPassword').val()
		var password = $("#profileNewPassword1").val()
		var confirmPassword = $("#profileNewPassword2").val()
		var defaultZipCode = $("#profileZipCode").val()
		var defaultTimeWindow = $("#profileTimeWindow").val()
		var formId = '#profileFeedback'
		var updateData = '  ' // do not remove the extra space here, because we slice later

		$('#profileSaving').show().delay(100);
		if (gather.global.currentDisplayName != displayName && displayName != '') {
			if (validateDisplayName(formId, displayName)) {
				updateData = updateData + '"displayName":"' + displayName + '", '
			} else {
				$('#profileSaving').hide();
				return
			}
		}

		if (password != '' ) {
			if (validatePassword(formId, password, confirmPassword)) {
				updateData = updateData + '"password":"' + password + '", '
				updateData = updateData + '"oldPassword":"' + oldPassword + '", '
			} else {
				$('#profileSaving').hide();
				return
			}
		}


		if (defaultZipCode != '') {
			if (validateZipCode(formId, defaultZipCode)) {
				updateData = updateData + '"defaultZip":' + defaultZipCode + ', '
			} else {
				$('#profileSaving').hide();
				return
			}
		}

		if (defaultTimeWindow != '') {
			if (defaultTimeWindow >= 1 && defaultTimeWindow <= 13) {
				updateData = updateData + '"defaultTimeWindow":' + defaultTimeWindow + ', '
			} else {
				$('#profileSaving').hide();
				return
			}
		}

		updateData = updateData.slice(0,-2)  // removing the last comma

		$.ajax({
		 	accepts: "application/json",
			type : "PUT",
			url : "rest/registrants/update",
			contentType: "application/json; charset=UTF-8",
			dataType: "json",
			data : '{'+updateData+'}',
			complete: function() {
                $('#profileSaving').hide();
            },
			success : function(returnvalue) {
				if (returnvalue.status == 0) {
					$(formId).html('Profile update successful!').show().delay(3000).hide(1000)
					if (gather.global.currentDisplayName != displayName) {
						gather.global.currentDisplayName = displayName
						sessionCheck();
						updateProfileHeader();
					}
				} else {
					$(formId).html(returnvalue.message)
				}
				$("#profileDoNotChangePassword").trigger('click');
			},
			error : function(jqXHR, textStatus, errorThrown) {
                var responseMessage = $.parseJSON(jqXHR.responseText).message;
                $(formId).html(responseMessage).show().delay(3000).hide(1000);
                $("#profileDoNotChangePassword").trigger('click');
            }
		});

	});

	$('#profileBack').on('click', function() {
		$("#profile").hide();
		$("#profileFeedback").hide();
		$("#profileDoNotChangePassword").trigger('click');
		$("#map").show();
		map._onResize(); 
	});
}

function loadProfilePasswordForm() {
	$('#profileChangePassword').on('click', function() {
		$(".profilePasswordForm").show("slow");
	});
	$('#profileDoNotChangePassword').on('click', function() {
		$(".profilePasswordForm").hide("slow");
		$("#profileCurrentPassword").val('');
		$("#profileNewPassword1").val('');
		$("#profileNewPassword2").val('');
	});
}

function updateProfileHeader(){
	$("#profileName").html('').html(gather.global.currentDisplayName + "'s Profile");
}

function loadChangePassword() {
	$('#profileChangePassword').on('click', function() {
		$("#profile").show();
		$("#map").hide();
		updateProfileHeader();
	});
	$('#profileChangePassword').on('click', function() {
		$("#profile").hide();
		$("#map").show();
	});
}
