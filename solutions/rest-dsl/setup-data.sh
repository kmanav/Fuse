mysql --host=infrastructure.lab.example.com -ubookstore -predhat -t -e "delete from  bookstore.OrderItem where id = 99"
mysql --host=infrastructure.lab.example.com -ubookstore -predhat -t -e "delete from bookstore.order_ where id = 99"
mysql --host=infrastructure.lab.example.com -ubookstore -predhat -t -e "delete from  bookstore.Customer where id = 99"
mysql --host=infrastructure.lab.example.com -ubookstore -predhat -t -e "delete from bookstore.Address where id = 99"

mysql --host=infrastructure.lab.example.com -ubookstore -predhat -t -e "insert into bookstore.Customer (id,admin,email,firstName,lastName,password,username) values (99,0,'user01@example.com','User','Name','chageme','user01');"
mysql --host=infrastructure.lab.example.com -ubookstore -predhat -t -e "insert into bookstore.Address (id,city,country,postalCode,state,streetAddress1,streetAddress2,streetAddress3) values (99,'Raleigh','USA','27601','NC','100 East Davie Street','','');"
mysql --host=infrastructure.lab.example.com -ubookstore -predhat -t -e "update bookstore.Customer set ship_addr_id = 99, bill_addr_id = 99 where id = 99;"
mysql --host=infrastructure.lab.example.com -ubookstore -predhat -t -e "insert into bookstore.order_ (id,delivered,discount,orderDate,cust_id,payment_id) values (99,0,0.00,now(),99,null);"
mysql --host=infrastructure.lab.example.com -ubookstore -predhat -t -e "insert into bookstore.OrderItem (id,extPrice,quantity,item_id,order_id) values (99,15.99,2,1,99);"
echo "Data setup complete!"