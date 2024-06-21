INSERT INTO accounts(
id, 
account_holder_user_id, 
branch_id, 
account_manager_user_id, 
iban, 
creation_date, 
balance,
interest, 
balance_updated_date, 
currency, 
TYPE, 
status) VALUES (
1, 
'john.doe@doe.com', 
'111', 
'boss@boss.com', 
'iban', 
'2024-01-01', 
0, 
0, 
'2024-01-01', 
'EUR',
0,
0);

ALTER SEQUENCE accounts_seq RESTART WITH 500;