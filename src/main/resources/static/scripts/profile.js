$(document).ready(function() {
	loadProfilePage();
	rightPaneSelect();
	loadProfilePasswordForm();
	submitProfile();
	controlCategory();
});

var categoryIndex = 1;

function rightPaneSelect() {
	$("#profile").hide();
	$("#map").show();
}

function loadProfilePage() {
	$('#myProfile').on('click', function() {
		$("#profile").show();
		$("#map").hide();
		$.ajax({
			async: false,
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
					var showEventsAroundZipCode = returnvalue.result.showEventsAroundZipCode
					var categories = returnvalue.result.preferences
					$("#profileDisplayName").val(displayName)
					$("#profileZipCode").val(defaultZip)
					$("#profileTimeWindow").val(defaultTimeWindow)
					$('#zipCodeCheckbox').prop('checked', showEventsAroundZipCode);
					$("#profile").show();
					$("#map").hide();
					updateProfileHeader();
					$("#profileCategories").empty();
					setUpCategory(categories.length)
					for (var i = 1; i < categories.length+1; i++) {
						var htmlID = "#profileCategories" + i
						$(htmlID).val(categories[i-1])
					}
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

function setUpCategory(index) {
	// reset the field 
	categoryIndex = index
	$('#profileMyCategories').html('')
	if (index == 1) {
		$('#removeCategory').prop("disabled",true);
		$('#addCategory').prop("disabled",false);
	} else if (index == 4) {
		$('#removeCategory').prop("disabled",false);
		$('#addCategory').prop("disabled",true);
	} else {
		$('#removeCategory').prop("disabled",false);
		$('#addCategory').prop("disabled",false);
	}
	for (var i = 1; i < index+1; i++) {
		displayCategoryField(i)
	}
	
}

function controlCategory() {
	$('#addCategory').on('click', categoryIndex, function (){
		categoryIndex += 1
		if (categoryIndex <= 4) {
			displayCategoryField(categoryIndex)
			$('#removeCategory').prop("disabled",false);
		}
		if (categoryIndex == 4) {
			$('#addCategory').prop("disabled",true);
		}	
	});

	$('#removeCategory').on('click', categoryIndex, function() {
		if (categoryIndex >= 1) {
			removeCategoryField(categoryIndex)
			$('#addCategory').prop("disabled",false);
			categoryIndex -= 1
		} 
		if (categoryIndex == 1) {
			$('#removeCategory').prop("disabled",true);
		}
	});
}

function setUpCategoryOptions(index) {
	var newOptions={};
	var catArray = gather.global.categories;
	var htmlID = "#profileCategories" + index
	$(htmlID).empty();
	$(htmlID).append($("<option></option>")
			     .attr("value", "").text("---------"));
	for (var i = 0; i < catArray.length; i++) {
		newOptions[catArray[i].name] = catArray[i].name;
		$(htmlID).append($("<option></option>")
			     .attr("value", catArray[i].name).text(catArray[i].name));
	}
}

function displayCategoryField(index) {
	var htmlID = 'profileCategories' + index
	var divID = 'profileCategoriesDiv' + index
	var jsID = '#profileCategories' + index
	var categoryField = '<div id="' + divID + '"><select style="margin-bottom: 5px; width:100%;" class="form-control" id="' + htmlID + '"/><div>'
	$('#profileCategories').append(categoryField)
	setUpCategoryOptions(index);
}

function removeCategoryField(index) {
	var jsID = '#profileCategoriesDiv' + index
	$(jsID).remove()
}

function submitProfile() {
	$('#profileSubmit').on('click', function() {
		var displayName = $("#profileDisplayName").val()
		var oldPassword = $('#profileCurrentPassword').val()
		var password = $("#profileNewPassword1").val()
		var confirmPassword = $("#profileNewPassword2").val()
		var defaultZipCode = $("#profileZipCode").val()
		var defaultTimeWindow = $("#profileTimeWindow").val()
		var categories = [];
		var categoriesCheck = $('#categoriesCheckbox').is(':checked')
		var zipCodeCheck = $('#zipCodeCheckbox').is(':checked')
		var timeWindowCheck = $('#timeWindowCheckbox').is(':checked')
		for (var i = 1; i < categoryIndex+1; i++) {
			var selectID = '#profileCategories' + i
			categories.push($(selectID).val())
		}

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

		if (categoriesCheck) {
			updateData = updateData + '"showEventsAroundZipCode":' + categoriesCheck + ', '
		}

		// if (zipCodeCheck) {
		// 	updateData = updateData + '"showEventsAroundZipCode":' zipCodeCheck + ''
		// }

		// if (timeWindowCheck) {
		// 	updateData = updateData + '"showEventsAroundZipCode":' timeWindowCheck + ''
		// }

		if (emptyStringArray(categories)) {
			updateData = updateData + '"preferences": ['
			for (var i = 0; i < categories.length; i++) {
				if (categories[i] !== "") {
					updateData = updateData + '"' + categories[i] + '", '
				}
			}
			updateData = updateData.slice(0,-2)  // removing the last comma
			updateData = updateData + '], '
		}

		updateData = updateData.slice(0,-2)  // removing the last comma

		$.ajax({
			async: false,
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
					$(formId).css("color", "green")
					$(formId).html('Profile update successful!').show().delay(3000).hide(1000)
					if (gather.global.currentDisplayName != displayName) {
						gather.global.currentDisplayName = displayName
						sessionCheck();
						updateProfileHeader();
					}
				} else {
					$(formId).css("color", "red")
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
		// not sure who added this? this function is not working
		// map._onResize(); 
	});
}

function emptyStringArray(my_arr){
   for(var i=0;i<my_arr.length;i++) {
       if (my_arr[i] !== "")  {
       	    return true;
       }   
   }
    return false;
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
