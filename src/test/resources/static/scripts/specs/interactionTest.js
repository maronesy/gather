
describe("front end email/password tests", function () {

    it("correct email", function() {
    	var isEmail = validateEmail("#dummyFormId","hello@gmail.com");
    	expect(isEmail).toBe(true);
    });

    it("two dot email (@korea.co.kr, @spain.go.sp)", function() {
    	var isEmail = validateEmail("#dummyFormId","hello@korea.co.kr");
    	expect(isEmail).toBe(true);
    });

    it("email missing @", function() {
    	var isEmail = validateEmail("#dummyFormId","hellogmail.com");
    	expect(isEmail).toBe(false);
    });

    it("email missing .", function() {
    	var isEmail = validateEmail("#dummyFormId","hello@gmailcom");
    	expect(isEmail).toBe(false);
    });

    it("correct password", function() {
    	var validPass = validatePassword("#dummyFormId","password","password");
    	expect(validPass).toBe(true);
    });

    it("short password", function() {
    	var validPass = validatePassword("#dummyFormId","as","as");
    	expect(validPass).toBe(false);
    });

    it("long password", function() {
    	var validPass = validatePassword("#dummyFormId","assadfwefwawefwafefsefawefe","assadfwefwawefwafefsefawefe");
    	expect(validPass).toBe(false);
    });

    it("correct display name", function() {
    	var validPass = validateDisplayName("#dummyFormId","helloworld");
    	expect(validPass).toBe(true);
    });

    it("short display name", function() {
    	var validPass = validateDisplayName("#dummyFormId","as");
    	expect(validPass).toBe(false);
    });

    it("long display name", function() {
    	var validPass = validateDisplayName("#dummyFormId","assadfwefwawefwafefsefawefe");
    	expect(validPass).toBe(false);
    });

});


describe("zip code button click test", function() {
	beforeEach(function () {
			loadFixtures('rightpane/map.html');
		});

	  it("empty zip code", function() {
	    runs(function() {
	    	$("#enterZip").trigger('click');
	    });

	    waitsFor(function() {
	    	return ($('#zipCodeErrorBox').html() == 'Zip code field is empty')
	    }, "no response after clicking #enterZip", 2000);

	    runs(function() {
	    	expect($('#zipCodeErrorBox').html()).toBe('Zip code field is empty')
	    });
	  });

	  it("non-five-digit zip code", function() {
		    runs(function() {
		    	$("#zipCode").val('1034');
		    	$("#enterZip").trigger('click');
		    });

		    waitsFor(function() {
		    	return ($('#zipCodeErrorBox').html() == 'Zip code must be five digits')
		    }, "no response after clicking #enterZip", 2000);

		    runs(function() {
		    	expect($('#zipCodeErrorBox').html()).toBe('Zip code must be five digits')
		    });
		  });

	  it("non-digit zip code", function() {
		runs(function() {
			$("#zipCode").val('asdff');
			$("#enterZip").trigger('click');
		});

		waitsFor(function() {
			return ($('#zipCodeErrorBox').html() == 'Zip code must be five digits')
		}, "no response after clicking #enterZip", 2000);

		runs(function() {
			expect($('#zipCodeErrorBox').html()).toBe('Zip code must be five digits')
		});
	  });

//	  it("non-existing zip code", function() {
//		    runs(function() {
//		    	$("#zipCode").val('13025');
//		    	$("#enterZip").trigger('click');
//		    });
//
//		    waitsFor(function() {
//		    	return ($('#zipCodeErrorBox').html() == 'Zip code does not exist')
//		    }, "no response after clicking #enterZip", 10000);
//
//		    runs(function() {
//		    	var htmlValue = $('#zipCodeErrorBox').html();
//		    	expect(htmlValue).toBe('Zip code does not exist')
//		    });
//		  });
	});

describe("register form tests", function () {
	beforeEach(function () {
		//jasmine-jquery.js defines Fixtures root to be
		//'src/main/resources/templates' on line 40.
		loadFixtures('registerform.html');
		jasmine.Ajax.installMock();
		$("#inputEmail").val('test@email.com');
    	$("#inputPassword1").val('password1');
    	$("#inputPassword2").val('password2');
    	$("#inputDisplayName").val('iamthetestuser');
	});

	afterEach(function() {
		jasmine.Ajax.uninstallMock();
	});

//    it("registration success", function() {
//    	// submitting the form with information in beforeEach
//    	spyOn($, "ajax");
//    	$("#inputPassword2").val('password1');
//    	runs(function() {
//    		$("#registerFormSubmit").trigger('click');
//    	});
//
//    	waitsFor(function() {
//	    	return ($.ajax.mostRecentCall.args[0] == 0)
//	    }, 'register form was never clicked', 2000);
//
//		runs(function() {
//			expect($.ajax.mostRecentCall.args[0]["url"]).toEqual(configuration.url);
////			expect($('#formFeedback').html()).toBe('Registration Success!')
//		});
//    });


	it("clicks the #registerFormSubmit", function() {
		spyOnEvent($('#registerFormSubmit'), 'click');
    	$("#registerFormSubmit").trigger('click');
    	expect('click').toHaveBeenTriggeredOn($('#registerFormSubmit'));
	});

    it("check registration form confirm password", function() {
    	// submitting the form with information in beforeEach
    	runs(function() {
    		$("#registerFormSubmit").trigger('click');
    	});

    	waitsFor(function() {
	    	return ($('#formFeedback').html() == 'Passwords do not match')
	    }, 'register form was never clicked', 2000);

		runs(function() {
			expect($('#formFeedback').html()).toBe('Passwords do not match')
		});
    });

//    it("check registration form duplicate email", function() {
//    	$("#inputEmail").val('testuser@email.com');
//    	$("#inputPassword2").val('password1');
//    	runs(function() {
//    		$("#registerFormSubmit").trigger('click');
//    	});
//
//    	waitsFor(function() {
//	    	return ($('#formFeedback').html() == 'This email is in use.')
//	    }, 'register form was never clicked', 2000);
//
//		runs(function() {
//			expect($('#formFeedback').html()).toBe('This email is in use.')
//		});
//    });

    it("display name cannot contain spaces", function() {
    	// submitting the form with information in beforeEach
    	$("#inputPassword2").val('password1');
    	$("#inputDisplayName").val('iamt hetestuser');
    	runs(function() {
    		$("#registerFormSubmit").trigger('click');
    	});

    	waitsFor(function() {
	    	return ($('#formFeedback').html() == 'Display name cannot have spaces')
	    }, 'register form was never clicked', 2000);

		runs(function() {
			expect($('#formFeedback').html()).toBe('Display name cannot have spaces')
		});
    });

    it("registration form exists", function() {
    	var formExists = false
    	if ($('#registration').length){
    		formExists = true
        }
    	expect(formExists).toBe(true)
    });

    it("jasmine enters values to registration form", function() {
    	// expect the form to contain information in beforeEach
    	var email = $('#inputEmail').val();
    	var password1 = $('#inputPassword1').val();
    	var password2 = $('#inputPassword2').val();
    	var displayName = $('#inputDisplayName').val();
    	expect(email).toBe('test@email.com')
    	expect(password1).toBe('password1')
    	expect(password2).toBe('password2')
    	expect(displayName).toBe('iamthetestuser')
    });


});