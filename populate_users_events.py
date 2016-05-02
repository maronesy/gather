import faker as fk
import numpy as np
import time

SD_zipcode = [91901,91902, 91903, 91905, 91906, 91908, 91909, 91910, 91911, 91912, 91913, 91914, 91915, 
91916, 91917, 91921, 91931, 91932, 91933, 91934, 91935, 91941, 91942, 91943, 91944, 91945, 91946, 91947, 
91948, 91950, 91951, 91962, 91963, 91976, 91977, 91978, 91979, 91980, 91987, 92003, 92004, 92007, 92008, 
92009, 92010, 92011, 92013, 92014, 92018, 92019, 92020, 92021, 92022, 92023, 92024, 92025, 92026, 92027, 
92028, 92029, 92030, 92033, 92036, 92037, 92038, 92039, 92040, 92046, 92049, 92051, 92052, 92054, 92055, 
92056, 92058, 92057, 92059, 92060, 92061, 92064, 92065, 92066, 92067, 92068, 92069, 92070, 92071, 92072, 
92074, 92075, 92078, 92079, 92081, 92082, 92083, 92084, 92085, 92086, 92088, 92090, 92091, 92092, 92093, 
92096, 92101, 92102, 92103, 92104, 92105, 92106, 92107, 92108, 92109, 92110, 92111, 92112, 92113, 92114, 
92115, 92116, 92117, 92118, 92119, 92120, 92121, 92122, 92123, 92124, 92126, 92127, 92128, 92129, 92130, 
92131, 92132, 92134, 92135, 92136, 92137, 92138, 92139, 92140, 92142, 92143, 92145, 92147, 92149, 92150, 
92152, 92153, 92154, 92155, 92158, 92159, 92160, 92161, 92162, 92163, 92164, 92165, 92166, 92167, 92168, 
92169, 92170, 92171, 92172, 92173, 92174, 92175, 92176, 92177, 92178, 92179, 92182, 92184, 92186, 92187, 
92190, 92191, 92192, 92193, 92194, 92195, 92196, 92197, 92198, 92199 ]

lat_min = 32.5
lat_max = 32.97
lon_min = -117.25
lon_max = -116.85

SD_city = ['Alpine', 'Bonita', 'Borrega Springs', 'Borrego Springs', 'Campo', 'Cardiff', 
'Cardiff-by-the-Sea', 'Carlsbad', 'Chula Vista', 'Coronado', 'Del Mar', 'El Cajon', 'Encinitas', 
'Escondido', 'Fallbrook', 'Hillcrest', 'Imperial', 'Imperial Beach', 'Jamul', 'Julian', 'La Mesa', 
'Lakeside', 'Lemon Grove', 'Leucadia', 'Live Oak Springs', 'Mission Bay', 'National City', 
'Oceanside', 'Pacific Beach', 'Pala', 'Point Loma', 'Poway', 'Ramona', 'Rancho Bernardo', 
'Rancho Santa Fe', 'San Clemente Island', 'San Diego', 'San Marco', 'San Marcos', 'San Ysidro', 
'Santee', 'Solana Beach', 'Spring Valley', 'Valley Center', 'Vista']

state = 'CA'
num = 150
user_num = 50
f = open('fakeuser.txt','w+')

# Registrant aUser = new Registrant("testuser@email.com","password","testDisplayName",10L,3,10000);
# Registrant registrantResult = this.registrantRepo.save(aUser);

time_win = [1, 2, 3, 4, 8, 12, 24, 72, 168 ,336, 730, 2190, 8760]
for i in range(num):
    fake_person = fk.Faker()
    email = fake_person.email()
    password = 'password'
    user_name = fake_person.user_name()
    reliability = np.random.randint(1,11)
    time_window = np.random.randint(0,13)
    zip_code = SD_zipcode[np.random.randint(0,len(SD_zipcode))]
    f.write('Registrant User%i = new Registrant("%s","%s","%s",%i,%i);\n'\
        % (i, email, password, user_name, time_win[time_window], zip_code))
    f.write('this.registrantRepo.save(User%i);\n' % (i))

f.close()

# Event newEvent2 = new Event("Test2");
# Location newLoc2 = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 32.780, -117.03);
# newEvent.addOwner(aUser);
# newEvent2.setLocation(newLoc2);
# Occurrence newOccur2 = new Occurrence("Second", new Timestamp(DateTime.now().plusDays(2).getMillis()));
# newEvent2.addOccurrence(newOccur2); 
# Occurrence newOccur3 = new Occurrence("Second2", new Timestamp(DateTime.now().plusDays(5).getMillis()));
# newEvent2.addOccurrence(newOccur3); 
# newEvent2.setDescription("lets swim!");
# newEvent2.setCategory("Swim");
# this.eventRepo.save(newEvent2);

f = open('fakeevents.txt','w+')
occurrence = ['Meeting', 'Competition', 'Practice', 'Practice', 'Scrimmage' ]
categories = ['arts', 'book', 'community', 'education', 'fitness', 
              'food', 'games', 'hobbies', 'movies', 'music', 'outdoors', 
              'sports', 'tech']

for i in range(num):
    fake_data = fk.Factory.create()
    title = fake_data.text(max_nb_chars=40)
    word = fake_data.sentence(nb_words=3)
    address = fake_data.street_address()
    city = SD_city[np.random.randint(0,len(SD_city))]
    zip_code = SD_zipcode[np.random.randint(0,len(SD_zipcode))]
    lat = np.random.uniform(lat_min,lat_max)
    lon = np.random.uniform(lon_min,lon_max)
    user_id = np.random.randint(0,user_num)
    description = fake_data.text(max_nb_chars=100)
    category = categories[np.random.randint(0,13)]


    f.write('Event newEvent%i = new Event("%s");\n' % (i,title))
    f.write('Location newLoc%i = new Location("%s", "%s", "%s", "%s", "%i", %f, %f);\n' %
        (i, word, address, city, state, zip_code, lat, lon))
    f.write('newEvent%i.addOwner(User%i);\n' % (i,user_id))
    f.write('newEvent%i.addParticipant(User%i);\n' % (i,user_id))
    for k in range(np.random.randint(1,10)):
        f.write('newEvent%i.addParticipant(User%i);\n' % (i,np.random.randint(0,user_num)))
    f.write('newEvent%i.setLocation(newLoc%i);\n' % (i, i))
    for j in range(np.random.randint(1,5)):
        datetime = np.random.randint(0,150)
        f.write('Occurrence newOccur%i_%i = new Occurrence("%s", new Timestamp(DateTime.now().plusDays(%i).getMillis()));\n' %
            (i, j, occurrence[j], datetime))
        f.write('newEvent%i.addOccurrence(newOccur%i_%i);\n' % (i,i,j))

    f.write('newEvent%i.setDescription("%s");\n' % (i,description))
    f.write('newEvent%i.setCategory(%s);\n' % (i,category))
    f.write('this.eventRepo.save(newEvent%i);\n' % i)

# for i in range(num):
#     f.write('this.eventRepo.save(newEvent%i);\n' % i)
# aUser.joinEvent(testEvent);

# f = open('fakejoin.txt','w+')

# for i in range(user_num):
#     for j in range(np.random.randint(1,10)):
#         f.write('newEvent%i.addParticipant(User%i);\n' % (i,np.random.randint(0,user_num)))

f.close()

