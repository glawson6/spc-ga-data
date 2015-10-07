select count(*) from restaurant_details_stage;
select count(*) from restaurant_location_stage;

select * from restaurant_details_stage;
select * from restaurant_location_stage 



select count(*) from restaurant_details;
select count(*) from restaurant_location;

select * from restaurant_details;
select * from restaurant_location 


select count(*) from restaurant_details;
select count(*) from restaurant_location 

select count(*) from restaurant_details_stage;
select count(*) from restaurant_location_stage; 

delete from restaurant_details_stage;
delete from restaurant_location_stage;
delete from restaurant_details;
delete from restaurant_location;

select * from restaurant_details rdetail
JOIN restaurant_location rloc on rdetail.restaurant_id = rloc.restaurant_id 
where (company_name like '%AFC%' OR company_name_alt like '%AFC%') AND company_address like '

select * from restaurant_details rdetail where (company_name like '%AFC%' OR company_name_alt like '%AFC%') AND company_address like ''

alter table restaurant_location alter latitude set data type float8
alter table restaurant_location alter longitude set data type float8

select restaurant0_.id as id1_6_, restaurant0_.company_name_alt as company_2_6_, restaurant0_.company_address as company_3_6_, 
restaurant0_.company_grade as company_4_6_, restaurant0_.company_name as company_5_6_, restaurant0_.company_phone as company_6_6_, 
restaurant0_.company_score as company_7_6_, restaurant0_.found_by as found_by8_6_, restaurant0_.image_url as image_ur9_6_, 
restaurant0_.inspection_link as inspect10_6_, restaurant0_.inspection_report_link as inspect11_6_, 
restaurant0_.inspection_search_link as inspect12_6_, restaurant0_.last_updated as last_up13_6_, restaurant0_.rating as rating14_6_, 
restaurant0_.rating_comments_link as rating_15_6_, restaurant0_.restaurant_id as restaur16_6_, restaurant0_.status as status17_6_ 
from restaurant_details restaurant0_ where (restaurant0_.company_name like '%White%' or restaurant0_.company_name_alt like '%White%')
and (restaurant0_.company_address like '%3172%')


select * from restaurant_details restaurant0_ where (restaurant0_.company_name like '%White%' or restaurant0_.company_name_alt like '%White%')
and (restaurant0_.company_address like '%3172%')
select * from restaurant_details where inspection_report_link IS NULL
