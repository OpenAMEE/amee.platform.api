INSERT INTO DUMMY_ENTITY (ID, CREATED, MODIFIED, UID, DUMMY_TEXT) VALUES(1, '2009-03-20 18:53:47', '2009-03-20 18:53:47', '655B1AD17733', 'Some dummy text.');

-- Dummy 'dual' table.
create table dual (dummy varchar(1));
insert into dual (dummy) values ('X');
