/*Global Variables init*/
var gather = gather || {};
var ViewingNearByEvents=0;
var ViewingJoinedEvents=1;
var ViewingOwnedEvents=2;
gather.global = {};
gather.global.currentDisplayName="Gather";
gather.global.nearEvents = null;
gather.global.joinedEvents = null;
gather.global.ownedEvents = null;
gather.global.email = null;
gather.global.session = {};
gather.global.session.signedIn = false;
gather.global.currentEventList = ViewingNearByEvents;
gather.global.categories = [];

