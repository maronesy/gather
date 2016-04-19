$(document).ready(function() {
    locateMe();
    sessionCheck();
    resizeLayout();
    resizeMap();
    enterZip();
    registerBox();
    signUp();
    signIn();
    signOut();
    onLoadSessionCheck();
    headerSelect();
    loadCategories();
});

function loadCategories(){
    $.ajax({
        accepts: "application/json",
        type : "GET",
        url : "api/categories",
        contentType: "application/json; charset=UTF-8",
        success : function(returnvalue) {
            var categories=returnvalue._embedded.categories;
            gather.global.categories = categories;
        }
    });
}

function resizeMap() {
    var cw = $('#map-canvas').width()*.75;
    $('#map-canvas').css({'height':cw+'px'});
}

function resizeLayout() {
    $('#resizeLayout').on('click', function() {
        if ($('.rightPane').hasClass("col-lg-7")) {
            $('#resizeLayout').removeClass('glyphicon-resize-small').addClass('glyphicon-resize-full');
            $('.rightPane').switchClass("col-lg-7", "col-lg-5");
            $('.rightPane').switchClass("col-md-7", "col-md-5");
            setTimeout(
                    function(){
                        $('.leftPane').switchClass("col-lg-5", "col-lg-7");
                        $('.leftPane').switchClass("col-md-5", "col-md-7");
                    }, 200);
            setInterval(
                    function(){
                        resizeMap();
                    }, 10);
        } else if ($('.rightPane').hasClass("col-lg-5")) {
            $('#resizeLayout').removeClass('glyphicon-resize-full').addClass('glyphicon-resize-small');
            $('.leftPane').switchClass("col-lg-7", "col-lg-5");
            $('.leftPane').switchClass("col-md-7", "col-md-5");
            setTimeout(
                    function(){
                        $('.rightPane').switchClass("col-lg-5", "col-lg-7");
                        $('.rightPane').switchClass("col-md-5", "col-md-7");
                    }, 200);
            setInterval(
                    function(){
                        resizeMap();
                    }, 10);
        }
        clearInterval(1000);
    });
}

function locateMe() {
    $('#locateMe').on('click', function() {
        mapManager.performAction();
    });
}

function enterZip() {
    $('#enterZip').on('click', function() {
        var zipCode = $('#zipCode').val();
        var zipCodeErrorBox = $(this).attr('href');
        $('#zipSearching').show();

        setTimeout(
            function(){
                if (zipCode == "") {
                    $(zipCodeErrorBox).fadeIn(100);
                    $('#zipCodeErrorBox').html('Zip code field is empty');
                } else {
                	validateZipCode(zipCodeErrorBox, zipCode) 
                }
                $('#zipSearching').hide();
            }, 100);
    });
    $('#zipCode').on('focus', function() {
        $(zipCodeErrorBox).fadeOut(100);
    });
}

function registerBox() {
    $('#registerButton').on('click', function() {
        $('#formFeedback').html('');
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
    errorBox = '#loginErrorBox'
    $('#loginFormSubmit').on(
            'click',
            function() {
                gather.global.email = $("#signInEmail").val();
                var password = $("#signInPassword").val();
                emailStatus = validateEmail("#loginErrorBox", gather.global.email)
                
                if (emailStatus) {
                    $.ajax({
                        accepts: "application/json",
                        type : "POST",
                        url : "rest/registrants/signin",
                        contentType: "application/json; charset=UTF-8",
                        dataType: "json",
                        data : '{ \
                            "email" : "' + gather.global.email + '", \
                            "password" : "' + password + '" \
                        }',
                        success : function(returnvalue) {
                            if (returnvalue.status == 0) {
                                resetSignInFields();
                                gather.global.session.signedIn = true
                                gather.global.currentDisplayName = returnvalue.displayName;
                                updateGreeting();
                                headerSelect();
                                window.location.href = "/"
                            } else {
                                $(errorBox).slideDown().delay(3000).slideUp();
                                $(errorBox).html(returnvalue.message);
                                resetSignInFields()
                            }
                        },
                        error : function(jqXHR, textStatus, errorThrown) {
                            var responseMessage = $.parseJSON(jqXHR.responseText).message;
                            $(errorBox).slideDown().delay(3000).slideUp();
                            $(errorBox).html(responseMessage);
                        }
                    });
                }
            });
    $('#signInEmail').on('focus', function() {
        $(errorBox).slideUp();
    });
    $('#signInPassword').on('focus', function() {
        $(errorBox).slideUp();
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
                                gather.global.session.signedIn = false;
                                window.location.href = "/"
                            } else {
                                if (returnvalue.status != 0) {
                                    alert(returnvalue.message)
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
                var formId = '#formFeedback'
                var emailStatus = false
                var passwordStatus = false
                var displayNameStatus = false
                if (displayName == "" || password == "" || confirmPassword == "" || email == "") {
                    $(formId).html('All the fields are required');
                } else {
                    emailStatus = validateEmail(formId, email)
                    passwordStatus = validatePassword(formId, password, confirmPassword)
                    displayNameStatus = validateDisplayName(formId, displayName)
                }
                if (emailStatus && passwordStatus && displayNameStatus) {
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
                                    $(formId).html('Registration Success!');
                                    $(registerBox).fadeOut(100);
                                    $('#mask , .register-popup').fadeOut(300, function() {
                                        $('#mask').remove();
                                    });
                                    resetRegisterFields();
                                    gather.global.session.signedIn = true
                                    gather.global.currentDisplayName = displayName;
                                    updateGreeting();
                                    headerSelect();
                                    window.location.href = "/"
                                } else {
                                        $(formId).html(returnvalue.message);
                                }
                            },
                            error : function(jqXHR, textStatus, errorThrown) {
                                var responseMessage = $.parseJSON(jqXHR.responseText).message;
                                $('#formFeedback').html(responseMessage);
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
        success : function(returnvalue, status, jqXHR) {
            if (returnvalue.status == 5) {
                gather.global.session.signedIn = true;
                gather.global.currentDisplayName = jqXHR.responseJSON.displayName;
                gather.global.email = jqXHR.responseJSON.email;
                updateGreeting();
                headerSelect();
            } else {
                gather.global.session.signedIn = false;
                headerSelect();
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert(jqXHR.status);
            alert(textStatus);
            alert(errorThrown);
        }
    });
}

function updateGreeting(){
    $("#greetings").html("Welcome "+gather.global.currentDisplayName);
}

function onLoadSessionCheck() {
    //alert(signedIn);
    $(window).unload(function() {
        headerSelect();
    });
}

function validateEmail(formId, email) {
    var x = email;
    var atpos = x.indexOf("@");
    var dotpos = x.lastIndexOf(".");
    if (atpos < 1 || dotpos < atpos + 2 || dotpos + 2 >= x.length) {
        $(formId).html('Please enter a valid email address').slideDown().delay(3000).slideUp();
        return false;
    } else {
        return true;
    }
}

function validatePassword(formId, password, confirmPassword){
    if (password.length < 7 || password.length > 21) {
        $(formId).html('Password must be between 6 and 21 characters').slideDown().delay(3000).slideUp();
        return false;
    } else if (password != confirmPassword) {
        $(formId).html('Passwords do not match').slideDown().delay(3000).slideUp();
        return false;
    } else {
        return true;
    }
}

function validateDisplayName(formId, displayName) {
    if ($.isNumeric(displayName)) {
        $(formId).html('Display name cannot be numeric').slideDown().delay(3000).slideUp();
    } else if (displayName.length < 5 || displayName.length > 15) {
        $(formId).html('Display name must be between than 5 and 15 characters').slideDown().delay(3000).slideUp();
        return false;
    } else if (displayName.indexOf(' ') >= 0) {
        $(formId).html('Display name cannot have spaces').slideDown().delay(3000).slideUp();
        return false;
    } else {
        return true;
    }
}

function validateZipCode(formId, zipCode) {
    if (zipCode.length != 5) {
        $(formId).html('Zip code must be five digits').slideDown().delay(3000).slideUp();
        return false;
    } else if (!($.isNumeric(zipCode))) {
        $(formId).html('Zip code must be five digits').slideDown().delay(3000).slideUp();
        return false;
    } else {
        var returnValue = mapManager.determineCoordByZipCode(zipCode);
        if (returnValue == -1) {
            $(formId).html('Zip code does not exist').slideDown().delay(3000).slideUp();
            return false;
        } else {
            $(formId).hide();
            return true
        }
    } 
}


function headerSelect() {
    if (gather.global.session.signedIn == true) {
        $("#headerOut").hide();
        $("#headerIn").show();
    } else if (gather.global.session.signedIn == false){
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
