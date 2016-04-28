/*Global Variables init*/
var gather = gather || {};
var ViewingNearByEvents=0;
var ViewingJoinedEvents=1;
var ViewingOwnedEvents=2;
gather.global = {};
gather.global.currentDisplayName="Gather";
gather.global.allDisplayName=[];
gather.global.nearEvents = [];
gather.global.joinedEvents = [];
gather.global.ownedEvents = [];
gather.global.email = null;
gather.global.session = {};
gather.global.session.signedIn = false;
gather.global.currentEventList = ViewingNearByEvents;
gather.global.categories = [];

