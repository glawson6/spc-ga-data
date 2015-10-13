mvn clean install -DskipTests && java -Xms1G -Xmx4G -Dhttp.port=8080 -Dspring.profiles.active=dev -DDATABASE_URL=postgres://ttis:ttis@centos-vm-local:5432/spoonscore -jar target/spc-ga-data-1.0-SNAPSHOT.jar

DATABASE_URL=postgres://postgres:2a2d922d81b097c21de11ed2d364d1ae@gregorylawson.net:6732/spoonscore



