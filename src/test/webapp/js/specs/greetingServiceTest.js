describe("the greeting service", function () {
	var greetingService;
 
	beforeEach(function(){
		greetingService = new GreetingService();
	});
 
    it("must create a valid greeting", function () {
        var greet = greetingService.greet("Max");
        expect(greet).toBe("Hello, Max");
    });
 
    it("must use an altered greeting", function(){
        greetingService.greeting = 'Hey';
        var greet = greetingService.greet("Kai");
        expect(greet).toBe("Hey, Kai");
    });
 
    it("must use fallback if no name given", function(){
    	var greet = greetingService.greet("Benson");
    	expect(greet).toBe("Hello, Benson");
    });
});