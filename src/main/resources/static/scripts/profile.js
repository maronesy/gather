$(document).ready(function() {
	loadProfilePage();
	leftPaneSelect();
	loadProfilePasswordForm();
	submitProfile();
});

function leftPaneSelect() {
	$("#profile").hide();
	$("#map").show();
}

function loadProfilePage() {
	$('#myProfile').on('click', function() {
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

		if (gather.global.currentDisplayName != displayName) {
			if (displayName != '' && validateDisplayName(formId, displayName)) {
				updateData = updateData + '"displayName":"' + displayName + '", '
			}
		}

		if (password != '' && validatePassword(formId, password, confirmPassword)) {
			updateData = updateData + '"password":"' + password + '", '
			updateData = updateData + '"oldPassword":"' + oldPassword + '", '
		}

		if (defaultZipCode != '' && validateZipCode(formId, defaultZipCode) ) {
			updateData = updateData + '"defaultZip":' + defaultZipCode + ', '
		}

		if (defaultTimeWindow != '' && defaultTimeWindow >= 1 && defaultTimeWindow <= 13) {
			updateData = updateData + '"defaultTimeWindow":' + defaultTimeWindow + ', '
		}

		updateData = updateData.slice(0,-2)  // removing the last comma

		$.ajax({
		 	accepts: "application/json",
			type : "PUT",
			url : "rest/registrants/update",
			contentType: "application/json; charset=UTF-8",
			dataType: "json",
			data : '{'+updateData+'}',
			success : function(returnvalue) {
				if (returnvalue.status == 0) {
					$(formId).html('Profile update successful!').delay(1000).hide(1000)
					if (gather.global.currentDisplayName != displayName) {
						sessionCheck();
					}
				} else {
					$(formId).html(returnvalue.message)
				}
			}
		});

	});

	$('#profileBack').on('click', function() {
		$("#profile").hide();
		$("#profileFeedback").hide();
		$("#map").show();
	});
}

function loadProfilePasswordForm() {
	$('#profileChangePassword').on('click', function() {
		$(".profilePasswordForm").show("slow");
	});
	$('#profileDoNotChangePassword').on('click', function() {
		$(".profilePasswordForm").hide("slow");
		$("#profileCurrentPassword").value("");
		$("#profileNewPassword").value("");
		$("#profileConfirmPassword").value("");
		$("#profilePasswordFeedback").value("");
	});
}

function updateProfileHeader(){
	$("#profileName").html(gather.global.currentDisplayName + "'s Profile");
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
