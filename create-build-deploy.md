mvn clean install -DskipTests && java -Dspring.profiles.active=dev -Dserver.port=7080 -D-server.address=0.0.0.0 -jar  target/spoonscore-web-0.1-SNAPSHOT.jar

 DSN: postgres://postgres:2a2d922d81b097c21de11ed2d364d1ae@172.17.0.5:5432/spoonscore
  dokku postgres:link spoonscore spoonscore-web
 dokku config:set spoonscore-web YELP_CONSUMER_KEY=wrqz-uCEaj5DgjOl2giosg YELP_CONSUMER_SECRET=1jeqMOLp6l3aBCLAWaI018rEuCA YELP_TOKEN=EZLa0hKc5n3V4vi42ymLxsHbEraY2Ps9 YELP_TOKEN_SECRET=QR8vlmIwvWXxVk71kroqicGtTZs
 dokku config:set spoonscore-web GOOGLE_API_KEY=AIzaSyDmbhivMvFx_-jKrlvwKy9OsAccMg6hZAU
 
 
 select count(zip_code) from zip_codes where county NOT in ('BANKS','BARROW','CLARKE','COBB','DAWSON','DEKALB','DOUGLAS','ELBERT','FORSYTH','FRANKLIN','GREENE','GWINNETT','HABERSHAM','HALL','HART','JACKSON','LUMPKIN','MADISON','MORGAN','OCONEE','OGLETHORPE','RABUN','STEPHENS','TOWNS','UNION','WALTON','WHITE')

 select zc from ZipCodes zc where zc.county NOT in ('BANKS','BARROW','CLARKE','COBB','DAWSON','DEKALB','DOUGLAS','ELBERT','FORSYTH','FRANKLIN','GREENE','GWINNETT','HABERSHAM','HALL','HART','JACKSON','LUMPKIN','MADISON','MORGAN','OCONEE','OGLETHORPE','RABUN','STEPHENS','TOWNS','UNION','WALTON','WHITE')

 dokku config:set spoonscoreweb YELP_CONSUMER_KEY=wrqz-uCEaj5DgjOl2giosg YELP_CONSUMER_SECRET=1jeqMOLp6l3aBCLAWaI018rEuCA YELP_TOKEN=EZLa0hKc5n3V4vi42ymLxsHbEraY2Ps9 YELP_TOKEN_SECRET=QR8vlmIwvWXxVk71kroqicGtTZs
 dokku config:set spoonscoreweb GOOGLE_API_KEY=AIzaSyDmbhivMvFx_-jKrlvwKy9OsAccMg6hZAU
  dokku config:set spoonscoreweb mule.host=0.0.0.0
