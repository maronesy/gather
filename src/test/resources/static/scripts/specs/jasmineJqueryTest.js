describe('jasmine-jquery.js tests', function () {
	var fixture;
	
	beforeEach(function () {
	    fixture = $('<div>some HTML code here</div>');
	});
	
	it('fixture object contains htmlText', function () {
		var htmlText = fixture[0].innerHTML;
		expect(htmlText).toBe('some HTML code here');
	});
	
	it('fixture object contains localName', function () {
		var localName = fixture[0].localName;
		expect(localName).toBe('div');
	});
});

describe("Asynchronous specs", function() {
	  var value, flag;

	  it("should support async execution of test preparation and expectations", function() {
	    runs(function() {
	      flag = false;
	      value = 0;

	      setTimeout(function() {
	        flag = true;
	      }, 500);
	    });

	    waitsFor(function() {
	      value++;
	      return flag;
	    }, "The Value should be incremented", 750);

	    runs(function() {
	      expect(value).toBeGreaterThan(0);
	    });
	  });
	});