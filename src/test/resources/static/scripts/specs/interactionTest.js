describe("interactionsTest", function () {
 
    it("validate email 1", function() {
    	var isEmail = validate_email("hello@gmail.com");
    	expect(isEmail).toBe(true);
    });
    
    it("validate email 2", function() {
    	var isEmail = validate_email("hellogmail.com");
    	expect(isEmail).toBe(false);
    });
    
    it("validate email 3", function() {
    	var isEmail = validate_email("hello@gmailcom");
    	expect(isEmail).toBe(false);
    });
      
    it("validate password 1", function() {
    	var validPass = validatePass("password");
    	expect(validPass).toBe(true);
    });
    
    it("validate password 2", function() {
    	var validPass = validatePass("as");
    	expect(validPass).toBe(false);
    });
    
    it("validate display name 1", function() {
    	var validPass = validatePass("helloworld");
    	expect(validPass).toBe(true);
    });
    
    it("validate display name 2", function() {
    	var validPass = validatePass("as");
    	expect(validPass).toBe(false);
    });
    
});